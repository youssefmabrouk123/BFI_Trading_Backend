package com.twd.BfiTradingApplication.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.twd.BfiTradingApplication.dto.PendingOrderDTO;
import com.twd.BfiTradingApplication.entity.*;
import com.twd.BfiTradingApplication.repository.CurrencyRepository;
import com.twd.BfiTradingApplication.repository.PendingOrderRepository;
import com.twd.BfiTradingApplication.repository.QuoteRepository;
import com.twd.BfiTradingApplication.repository.UserActionRepository;
import com.twd.BfiTradingApplication.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PendingOrderService {
    private static final Logger logger = LoggerFactory.getLogger(PendingOrderService.class);

    private final PendingOrderRepository pendingOrderRepository;
    private final CurrencyRepository currencyRepository;
    private final UserRepository userRepository;
    private final UserActionRepository userActionRepository;
    private final TradingService tradingService;
    private final QuoteRepository quoteRepository;
    private final SocketIOServer socketIOServer;

    @Autowired
    public PendingOrderService(
            PendingOrderRepository pendingOrderRepository,
            CurrencyRepository currencyRepository,
            UserRepository userRepository,
            UserActionRepository userActionRepository,
            TradingService tradingService,
            QuoteRepository quoteRepository,
            SocketIOServer socketIOServer) {
        this.pendingOrderRepository = pendingOrderRepository;
        this.currencyRepository = currencyRepository;
        this.userRepository = userRepository;
        this.userActionRepository = userActionRepository;
        this.tradingService = tradingService;
        this.quoteRepository = quoteRepository;
        this.socketIOServer = socketIOServer;
    }

    @Transactional
    public PendingOrderDTO createPendingOrder(PendingOrderDTO dto, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Currency baseCurrency = currencyRepository.findByIdentifier(dto.getBaseCurrencyIdentifier())
                .orElseThrow(() -> new RuntimeException("Base currency not found"));

        Currency quoteCurrency = currencyRepository.findByIdentifier(dto.getQuoteCurrencyIdentifier())
                .orElseThrow(() -> new RuntimeException("Quote currency not found"));

        validateOrderFields(dto);

        PendingOrder pendingOrder = new PendingOrder(
                baseCurrency,
                quoteCurrency,
                dto.getAmount(),
                dto.getTargetPrice(),
                dto.getOrderType(),
                dto.getTriggerType(),
                dto.getActionOnTrigger(),
                user
        );

        PendingOrder savedOrder = pendingOrderRepository.save(pendingOrder);

        UserAction userAction = new UserAction();
        userAction.setUser(user);
        userAction.setActionType("CREATE_PENDING_ORDER");
        userAction.setDetails(String.format("Created %s %s order for %s %s at price %s",
                dto.getTriggerType(), dto.getOrderType(), dto.getAmount(), baseCurrency.getIdentifier(), dto.getTargetPrice()));
        userAction.setAmount(dto.getAmount());
        userAction.setCurrency(baseCurrency);
        userAction.setActionTime(LocalDateTime.now());
        userActionRepository.save(userAction);

        broadcastPendingOrdersUpdate(userId);
        return toDTO(savedOrder);
    }

    private void validateOrderFields(PendingOrderDTO dto) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (dto.getTargetPrice() == null || dto.getTargetPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Target price must be greater than zero");
        }
        if (!"BUY".equalsIgnoreCase(dto.getOrderType()) && !"SELL".equalsIgnoreCase(dto.getOrderType())) {
            throw new IllegalArgumentException("Order type must be BUY or SELL");
        }
        if (!"STOP_LOSS".equalsIgnoreCase(dto.getTriggerType()) && !"TAKE_PROFIT".equalsIgnoreCase(dto.getTriggerType())) {
            throw new IllegalArgumentException("Trigger type must be STOP_LOSS or TAKE_PROFIT");
        }
        if (!"EXECUTE".equalsIgnoreCase(dto.getActionOnTrigger()) && !"NOTIFY".equalsIgnoreCase(dto.getActionOnTrigger())) {
            throw new IllegalArgumentException("Action on trigger must be EXECUTE or NOTIFY");
        }
    }

    public List<PendingOrderDTO> getPendingOrdersByUser(Integer userId) {
        return pendingOrderRepository.findByUserIdAndStatus(userId, "PENDING")
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<PendingOrderDTO> cancelPendingOrder(Integer orderId, Integer userId) {
        return pendingOrderRepository.findById(orderId)
                .filter(order -> order.getUser().getId().equals(userId))
                .map(order -> {
                    order.setStatus("CANCELLED");
                    PendingOrder cancelledOrder = pendingOrderRepository.save(order);

                    UserAction userAction = new UserAction();
                    userAction.setUser(order.getUser());
                    userAction.setActionType("CANCEL_PENDING_ORDER");
                    userAction.setDetails(String.format("Cancelled %s %s order for %s %s at price %s",
                            order.getTriggerType(), order.getOrderType(), order.getAmount(),
                            order.getBaseCurrency().getIdentifier(), order.getTargetPrice()));
                    userAction.setAmount(order.getAmount());
                    userAction.setCurrency(order.getBaseCurrency());
                    userAction.setActionTime(LocalDateTime.now());
                    userActionRepository.save(userAction);

                    broadcastPendingOrdersUpdate(userId);
                    return toDTO(cancelledOrder);
                });
    }

    @Scheduled(fixedRate = 3000) // Check every 3 seconds to match quote updates
    @Transactional
    public void checkAndExecutePendingOrders() {
        logger.info("Checking pending orders for execution...");

        List<PendingOrder> pendingOrders = pendingOrderRepository.findByStatus("PENDING");
        if (pendingOrders.isEmpty()) {
            return;
        }

        for (PendingOrder order : pendingOrders) {
            try {
                Optional<Quote> quoteOpt = quoteRepository.findLatestByCrossParity(
                        order.getBaseCurrency().getPk(),
                        order.getQuoteCurrency().getPk()
                );

                if (quoteOpt.isPresent()) {
                    Quote quote = quoteOpt.get();
                    BigDecimal currentPrice = "BUY".equalsIgnoreCase(order.getOrderType())
                            ? quote.getAskPrice()
                            : quote.getBidPrice();

                    if (shouldTriggerOrder(order, currentPrice)) {
                        processOrder(order, currentPrice);
                    }
                }
            } catch (Exception e) {
                logger.error("Error checking pending order ID {}: {}", order.getId(), e.getMessage(), e);
            }
        }
    }

    private boolean shouldTriggerOrder(PendingOrder order, BigDecimal currentPrice) {
        if ("BUY".equalsIgnoreCase(order.getOrderType())) {
            if ("STOP_LOSS".equalsIgnoreCase(order.getTriggerType())) {
                return currentPrice.compareTo(order.getTargetPrice()) >= 0;
            } else { // TAKE_PROFIT
                return currentPrice.compareTo(order.getTargetPrice()) <= 0;
            }
        } else { // SELL
            if ("STOP_LOSS".equalsIgnoreCase(order.getTriggerType())) {
                return currentPrice.compareTo(order.getTargetPrice()) <= 0;
            } else { // TAKE_PROFIT
                return currentPrice.compareTo(order.getTargetPrice()) >= 0;
            }
        }
    }

    private void processOrder(PendingOrder order, BigDecimal currentPrice) {
        if ("EXECUTE".equalsIgnoreCase(order.getActionOnTrigger())) {
            executeOrder(order, currentPrice);
        } else {
            notifyUser(order, currentPrice);
        }

        order.setStatus("EXECUTED");
        order.setExecutedAt(LocalDateTime.now());
        pendingOrderRepository.save(order);

        broadcastPendingOrdersUpdate(order.getUser().getId());
    }

    private void executeOrder(PendingOrder order, BigDecimal currentPrice) {
        logger.info("Executing {} {} order for user {} at price {}",
                order.getTriggerType(), order.getOrderType(), order.getUser().getId(), currentPrice);

        try {
            Transaction transaction = tradingService.executeTrade(
                    order.getUser().getId(),
                    order.getBaseCurrency().getPk(),
                    order.getQuoteCurrency().getPk(),
                    order.getAmount(),
                    order.getOrderType(),
                    currentPrice
            );

            logger.info("Successfully executed pending order ID {}, created transaction ID {}",
                    order.getId(), transaction.getId());

            sendOrderExecutionNotification(order, currentPrice);
        } catch (Exception e) {
            logger.error("Failed to execute pending order ID {}: {}", order.getId(), e.getMessage(), e);
            throw e;
        }
    }

    private void notifyUser(PendingOrder order, BigDecimal currentPrice) {
        logger.info("Notifying user {} about {} {} trigger at price {}",
                order.getUser().getId(), order.getTriggerType(), order.getOrderType(), currentPrice);
        sendOrderTriggerNotification(order, currentPrice);
    }

    private void sendOrderExecutionNotification(PendingOrder order, BigDecimal currentPrice) {
        try {
            socketIOServer.getRoomOperations("user-" + order.getUser().getId())
                    .sendEvent("orderExecution", toDTO(order));
        } catch (Exception e) {
            logger.error("Error sending order execution notification", e);
        }
    }

    private void sendOrderTriggerNotification(PendingOrder order, BigDecimal currentPrice) {
        try {
            socketIOServer.getRoomOperations("user-" + order.getUser().getId())
                    .sendEvent("orderTrigger", toDTO(order));
        } catch (Exception e) {
            logger.error("Error sending order trigger notification", e);
        }
    }

    private void broadcastPendingOrdersUpdate(Integer userId) {
        try {
            List<PendingOrderDTO> pendingOrders = getPendingOrdersByUser(userId);
            socketIOServer.getRoomOperations("user-" + userId)
                    .sendEvent("pendingOrdersUpdate", pendingOrders);
        } catch (Exception e) {
            logger.error("Error broadcasting pending orders update", e);
        }
    }

    private PendingOrderDTO toDTO(PendingOrder pendingOrder) {
        PendingOrderDTO dto = new PendingOrderDTO();
        dto.setId(pendingOrder.getId()); // Assurez-vous que cette ligne est bien présente
        dto.setBaseCurrencyId(pendingOrder.getBaseCurrency().getPk());
        dto.setBaseCurrencyIdentifier(pendingOrder.getBaseCurrency().getIdentifier());
        dto.setQuoteCurrencyId(pendingOrder.getQuoteCurrency().getPk());
        dto.setQuoteCurrencyIdentifier(pendingOrder.getQuoteCurrency().getIdentifier());
        dto.setAmount(pendingOrder.getAmount());
        dto.setTargetPrice(pendingOrder.getTargetPrice());
        dto.setOrderType(pendingOrder.getOrderType());
        dto.setTriggerType(pendingOrder.getTriggerType());
        dto.setActionOnTrigger(pendingOrder.getActionOnTrigger());
        dto.setStatus(pendingOrder.getStatus());
        dto.setCreatedAt(pendingOrder.getCreatedAt());
        dto.setExecutedAt(pendingOrder.getExecutedAt());
        return dto;
    }
}