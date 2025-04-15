package com.twd.BfiTradingApplication.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.twd.BfiTradingApplication.dto.NotificationDTO;
import com.twd.BfiTradingApplication.dto.PendingOrderDTO;
import com.twd.BfiTradingApplication.entity.*;
import com.twd.BfiTradingApplication.repository.*;
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
    private final NotificationRepository notificationRepository;
    private final SocketIOServer socketIOServer;

    @Autowired
    public PendingOrderService(
            PendingOrderRepository pendingOrderRepository,
            CurrencyRepository currencyRepository,
            UserRepository userRepository,
            UserActionRepository userActionRepository,
            TradingService tradingService,
            QuoteRepository quoteRepository,
            NotificationRepository notificationRepository,
            SocketIOServer socketIOServer) {
        this.pendingOrderRepository = pendingOrderRepository;
        this.currencyRepository = currencyRepository;
        this.userRepository = userRepository;
        this.userActionRepository = userActionRepository;
        this.tradingService = tradingService;
        this.quoteRepository = quoteRepository;
        this.notificationRepository = notificationRepository;
        this.socketIOServer = socketIOServer;
    }

    @Transactional
    public PendingOrderDTO createPendingOrder(PendingOrderDTO dto, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Currency baseCurrency = currencyRepository.findByIdentifier(dto.getBaseCurrencyIdentifier())
                .orElseThrow(() -> new RuntimeException("Base currency not found: " + dto.getBaseCurrencyIdentifier()));

        Currency quoteCurrency = currencyRepository.findByIdentifier(dto.getQuoteCurrencyIdentifier())
                .orElseThrow(() -> new RuntimeException("Quote currency not found: " + dto.getQuoteCurrencyIdentifier()));

        validateOrderFields(dto);

        PendingOrder pendingOrder = new PendingOrder();
        pendingOrder.setBaseCurrency(baseCurrency);
        pendingOrder.setQuoteCurrency(quoteCurrency);
        pendingOrder.setAmount(dto.getAmount());
        pendingOrder.setTargetPrice(dto.getTargetPrice());
        pendingOrder.setOrderType(dto.getOrderType().toUpperCase());
        pendingOrder.setTriggerType(dto.getTriggerType().toUpperCase());
        pendingOrder.setActionOnTrigger(dto.getActionOnTrigger().toUpperCase());
        pendingOrder.setUser(user);
        pendingOrder.setStatus("PENDING");
        pendingOrder.setCreatedAt(LocalDateTime.now());

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
        String orderType = dto.getOrderType().toUpperCase();
        if (!"BUY".equals(orderType) && !"SELL".equals(orderType)) {
            throw new IllegalArgumentException("Order type must be BUY or SELL");
        }
        String triggerType = dto.getTriggerType().toUpperCase();
        if (!"STOP_LOSS".equals(triggerType) && !"TAKE_PROFIT".equals(triggerType)) {
            throw new IllegalArgumentException("Trigger type must be STOP_LOSS or TAKE_PROFIT");
        }
        String actionOnTrigger = dto.getActionOnTrigger().toUpperCase();
        if (!"EXECUTE".equals(actionOnTrigger) && !"NOTIFY".equals(actionOnTrigger)) {
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

    @Scheduled(fixedRate = 3000) // Check every 3 seconds
    @Transactional
    public void checkAndExecutePendingOrders() {
        logger.info("Checking pending orders for execution...");

        List<PendingOrder> pendingOrders = pendingOrderRepository.findByStatus("PENDING");
        if (pendingOrders.isEmpty()) {
            logger.debug("No pending orders found.");
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
                    BigDecimal currentPrice = "BUY".equals(order.getOrderType())
                            ? quote.getAskPrice()
                            : quote.getBidPrice();

                    if (shouldTriggerOrder(order, currentPrice)) {
                        processOrder(order, currentPrice);
                    }
                } else {
                    logger.warn("No quote found for order ID: {}", order.getId());
                }
            } catch (Exception e) {
                logger.error("Error processing pending order ID {}: {}", order.getId(), e.getMessage(), e);
            }
        }
    }

    private boolean shouldTriggerOrder(PendingOrder order, BigDecimal currentPrice) {
        if ("BUY".equals(order.getOrderType())) {
            if ("STOP_LOSS".equals(order.getTriggerType())) {
                return currentPrice.compareTo(order.getTargetPrice()) >= 0;
            } else { // TAKE_PROFIT
                return currentPrice.compareTo(order.getTargetPrice()) <= 0;
            }
        } else { // SELL
            if ("STOP_LOSS".equals(order.getTriggerType())) {
                return currentPrice.compareTo(order.getTargetPrice()) <= 0;
            } else { // TAKE_PROFIT
                return currentPrice.compareTo(order.getTargetPrice()) >= 0;
            }
        }
    }

    private void processOrder(PendingOrder order, BigDecimal currentPrice) {
        if ("EXECUTE".equals(order.getActionOnTrigger())) {
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

            logger.info("Successfully executed order ID {}, created transaction ID {}",
                    order.getId(), transaction.getId());

            String message = String.format("Order %s %s executed for %s %s at price %s",
                    order.getTriggerType(), order.getOrderType(), order.getAmount(),
                    order.getBaseCurrency().getIdentifier(), currentPrice);
            Notification notification = new Notification(order.getUser(), "ORDER_EXECUTED", message);
            notificationRepository.save(notification);

            sendOrderExecutionNotification(order, notification);
        } catch (Exception e) {
            logger.error("Failed to execute order ID {}: {}", order.getId(), e.getMessage(), e);
            throw new RuntimeException("Order execution failed", e);
        }
    }

    private void notifyUser(PendingOrder order, BigDecimal currentPrice) {
        logger.info("Notifying user {} about {} {} trigger at price {}",
                order.getUser().getId(), order.getTriggerType(), order.getOrderType(), currentPrice);

        String message = String.format("%s %s triggered for %s %s at price %s",
                order.getTriggerType(), order.getOrderType(), order.getAmount(),
                order.getBaseCurrency().getIdentifier(), currentPrice);
        Notification notification = new Notification(order.getUser(), "ORDER_TRIGGERED", message);
        notificationRepository.save(notification);

        sendOrderTriggerNotification(order, notification);
    }

    private void sendOrderExecutionNotification(PendingOrder order, Notification notification) {
        try {
            NotificationDTO notificationDTO = toNotificationDTO(notification);
            String room = "user-" + order.getUser().getId();
            socketIOServer.getRoomOperations(room).sendEvent("orderExecution", notificationDTO);
            broadcastNotificationUpdate(order.getUser().getId());
        } catch (Exception e) {
            logger.error("Error sending order execution notification for order ID {}: {}", order.getId(), e.getMessage(), e);
        }
    }

    private void sendOrderTriggerNotification(PendingOrder order, Notification notification) {
        try {
            NotificationDTO notificationDTO = toNotificationDTO(notification);
            String room = "user-" + order.getUser().getId();
            socketIOServer.getRoomOperations(room).sendEvent("orderTrigger", notificationDTO);
            broadcastNotificationUpdate(order.getUser().getId());
        } catch (Exception e) {
            logger.error("Error sending order trigger notification for order ID {}: {}", order.getId(), e.getMessage(), e);
        }
    }

    private void broadcastPendingOrdersUpdate(Integer userId) {
        try {
            List<PendingOrderDTO> pendingOrders = getPendingOrdersByUser(userId);
            String room = "user-" + userId;
            socketIOServer.getRoomOperations(room).sendEvent("pendingOrdersUpdate", pendingOrders);
            logger.debug("Broadcasted pending orders update to room: {} with {} orders", room, pendingOrders.size());
        } catch (Exception e) {
            logger.error("Error broadcasting pending orders update for user {}: {}", userId, e.getMessage(), e);
        }
    }

    public List<NotificationDTO> getUserNotifications(Integer userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toNotificationDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markNotificationAsRead(Integer notificationId, Integer userId) {
        notificationRepository.findById(notificationId)
                .filter(n -> n.getUser().getId().equals(userId) && !n.isRead())
                .ifPresent(notification -> {
                    notification.setRead(true);
                    notificationRepository.save(notification);
                    broadcastNotificationUpdate(userId);
                    logger.info("Marked notification ID {} as read for user {}", notificationId, userId);
                });
    }

    @Transactional
    public void markAllNotificationsAsRead(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        boolean updated = false;
        for (Notification n : notifications) {
            if (!n.isRead()) {
                n.setRead(true);
                notificationRepository.save(n);
                updated = true;
            }
        }
        if (updated) {
            broadcastNotificationUpdate(userId);
            logger.info("Marked all notifications as read for user {}", userId);
        }
    }

    @Transactional
    public void deleteNotification(Integer notificationId, Integer userId) {
        notificationRepository.findById(notificationId)
                .filter(n -> n.getUser().getId().equals(userId))
                .ifPresent(notification -> {
                    notificationRepository.delete(notification);
                    broadcastNotificationUpdate(userId);
                    logger.info("Deleted notification ID {} for user {}", notificationId, userId);
                });
    }

    private void broadcastNotificationUpdate(Integer userId) {
        try {
            List<NotificationDTO> updatedNotifications = getUserNotifications(userId);
            String room = "user-" + userId;
            socketIOServer.getRoomOperations(room).sendEvent("notificationsUpdate", updatedNotifications);
            logger.debug("Broadcasted notification update to room: {} with {} notifications", room, updatedNotifications.size());
        } catch (Exception e) {
            logger.error("Error broadcasting notification update for user {}: {}", userId, e.getMessage(), e);
        }
    }

    @Transactional
    public void createAndBroadcastNotification(Notification notification) {
        notificationRepository.save(notification);
        NotificationDTO dto = toNotificationDTO(notification);
        String room = "user-" + notification.getUser().getId();
        String event = "ORDER_TRIGGERED".equals(notification.getType()) ? "orderTrigger" : "orderExecution";
        socketIOServer.getRoomOperations(room).sendEvent(event, dto);
        broadcastNotificationUpdate(notification.getUser().getId());
        logger.info("Created and broadcasted notification ID {} to room: {}", dto.getId(), room);
    }

    private PendingOrderDTO toDTO(PendingOrder pendingOrder) {
        PendingOrderDTO dto = new PendingOrderDTO();
        dto.setId(pendingOrder.getId());
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

    private NotificationDTO toNotificationDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUser().getId());
        dto.setType(notification.getType());
        dto.setMessage(notification.getMessage());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setRead(notification.isRead());
        return dto;
    }
}