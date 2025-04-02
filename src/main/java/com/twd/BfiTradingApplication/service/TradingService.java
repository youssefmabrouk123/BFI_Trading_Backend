package com.twd.BfiTradingApplication.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.twd.BfiTradingApplication.dto.PositionDTO;
import com.twd.BfiTradingApplication.entity.Currency;
import com.twd.BfiTradingApplication.entity.Position;
import com.twd.BfiTradingApplication.entity.Transaction;
import com.twd.BfiTradingApplication.exception.InsufficientFundsException;
import com.twd.BfiTradingApplication.exception.InvalidCurrencyException;
import com.twd.BfiTradingApplication.exception.TradingException;
import com.twd.BfiTradingApplication.repository.CurrencyRepository;
import com.twd.BfiTradingApplication.repository.PositionRepository;
import com.twd.BfiTradingApplication.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TradingService {
    private static final Logger logger = LoggerFactory.getLogger(TradingService.class);

    private final TransactionRepository transactionRepository;
    private final PositionRepository positionRepository;
    private final CurrencyRepository currencyRepository;
    private final SocketIOServer socketIOServer;

    private List<Position> positionList; // Liste en mémoire pour les positions

    @Autowired
    public TradingService(
            TransactionRepository transactionRepository,
            PositionRepository positionRepository,
            CurrencyRepository currencyRepository,
            SocketIOServer socketIOServer) {
        this.transactionRepository = transactionRepository;
        this.positionRepository = positionRepository;
        this.currencyRepository = currencyRepository;
        this.socketIOServer = socketIOServer;
    }

    @PostConstruct
    public void init() {
        positionList = positionRepository.findAll();
        logger.info("Initial positions loaded: {}", positionList.size());
        broadcastPositions();

        socketIOServer.addConnectListener(client -> {
            logger.info("Client connected: {}", client.getSessionId());
            client.sendEvent("positionsUpdate", positionList.stream().map(this::toDTO).collect(Collectors.toList()));
        });

        socketIOServer.addEventListener("requestPositions", String.class, (client, data, ackSender) -> {
            logger.info("Positions requested by client: {}", client.getSessionId());
            client.sendEvent("positionsUpdate", positionList.stream().map(this::toDTO).collect(Collectors.toList()));
        });
    }

    @Transactional
    public Transaction executeTrade(Integer baseCurrencyId, Integer quoteCurrencyId,
                                    BigDecimal mntAcht, String transactionType,
                                    BigDecimal marketPrice) {
        // Existing logic remains unchanged
        try {
            validateTradeParameters(baseCurrencyId, quoteCurrencyId, mntAcht, transactionType, marketPrice);

            Currency devAchn = getCurrency(baseCurrencyId, "Base");
            Currency devVen = getCurrency(quoteCurrencyId, "Quote");

            BigDecimal mntVen = calculateSoldAmount(mntAcht, marketPrice);

            TradeCurrencyDetails tradeDetails = determineTradeCurrencyDetails(
                    devAchn, devVen, mntAcht, mntVen, transactionType
            );

            Position boughtPosition = getOrCreatePosition(tradeDetails.boughtCurrency);
            Position soldPosition = getOrCreatePosition(tradeDetails.soldCurrency);

            validateFunds(soldPosition, tradeDetails.finalMntVen, transactionType);

            Transaction transaction = createTransaction(
                    tradeDetails.boughtCurrency,
                    tradeDetails.soldCurrency,
                    tradeDetails.finalMntAcht,
                    tradeDetails.finalMntVen,
                    marketPrice,
                    transactionType
            );

            updatePositions(boughtPosition, soldPosition, tradeDetails.finalMntAcht, tradeDetails.finalMntVen, transactionType);

            transactionRepository.save(transaction);
            positionRepository.save(boughtPosition);
            positionRepository.save(soldPosition);

            updatePositionList(boughtPosition, soldPosition);
            broadcastPositions();

            logger.info("Trade successful: {} {} of {} at market price {} for {} {}",
                    transactionType,
                    tradeDetails.finalMntAcht,
                    tradeDetails.boughtCurrency.getIdentifier(),
                    marketPrice,
                    tradeDetails.finalMntVen,
                    tradeDetails.soldCurrency.getIdentifier()
            );

            return transaction;

        } catch (InsufficientFundsException e) {
            throw new TradingException("Transaction failed: Insufficient funds to execute the trade.");
        } catch (InvalidCurrencyException e) {
            throw new TradingException("Transaction failed: Invalid currency identifiers provided.");
        } catch (Exception e) {
            throw new TradingException("An unexpected error occurred while executing the trade. Please try again later.");
        }
    }
    private void validateTradeParameters(Integer baseCurrencyId, Integer quoteCurrencyId,
                                         BigDecimal mntAcht, String transactionType,
                                         BigDecimal marketPrice) {
        if (baseCurrencyId == null || quoteCurrencyId == null) {
            throw new IllegalArgumentException("Currency IDs cannot be null");
        }
        if (mntAcht == null || mntAcht.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid trade amount");
        }
        if (marketPrice == null || marketPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid market price");
        }
        if (!"BUY".equalsIgnoreCase(transactionType) && !"SELL".equalsIgnoreCase(transactionType)) {
            throw new IllegalArgumentException("Invalid transaction type. Must be BUY or SELL.");
        }
    }

    private Currency getCurrency(Integer currencyId, String type) {
        return currencyRepository.findById(currencyId)
                .orElseThrow(() -> new RuntimeException(type + " currency not found"));
    }

    private BigDecimal calculateSoldAmount(BigDecimal mntAcht, BigDecimal marketPrice) {
        return mntAcht.multiply(marketPrice).setScale(4, RoundingMode.HALF_UP);
    }

    private static class TradeCurrencyDetails {
        Currency boughtCurrency;
        Currency soldCurrency;
        BigDecimal finalMntAcht;
        BigDecimal finalMntVen;

        TradeCurrencyDetails(Currency boughtCurrency, Currency soldCurrency,
                             BigDecimal finalMntAcht, BigDecimal finalMntVen) {
            this.boughtCurrency = boughtCurrency;
            this.soldCurrency = soldCurrency;
            this.finalMntAcht = finalMntAcht;
            this.finalMntVen = finalMntVen;
        }
    }

    private TradeCurrencyDetails determineTradeCurrencyDetails(
            Currency devAchn, Currency devVen,
            BigDecimal mntAcht, BigDecimal mntVen,
            String transactionType) {
        boolean isBuy = "BUY".equalsIgnoreCase(transactionType);
        Currency boughtCurrency = isBuy ? devAchn : devVen;
        Currency soldCurrency = isBuy ? devVen : devAchn;
        BigDecimal finalMntAcht = isBuy ? mntAcht : mntVen;
        BigDecimal finalMntVen = isBuy ? mntVen : mntAcht;
        return new TradeCurrencyDetails(boughtCurrency, soldCurrency, finalMntAcht, finalMntVen);
    }

    private Transaction createTransaction(Currency boughtCurrency, Currency soldCurrency,
                                          BigDecimal finalMntAcht, BigDecimal finalMntVen,
                                          BigDecimal marketPrice, String transactionType) {
        return new Transaction(boughtCurrency, soldCurrency, finalMntAcht, finalMntVen, marketPrice, transactionType.toUpperCase());
    }

    private Position getOrCreatePosition(Currency currency) {
        return positionRepository.findByCurrency(currency)
                .orElseGet(() -> {
                    Position newPosition = new Position(currency, BigDecimal.ZERO, BigDecimal.ZERO);
                    return positionRepository.save(newPosition);
                });
    }

    private void validateFunds(Position soldPosition, BigDecimal mntVen, String transactionType) {
        if (soldPosition.getMntDev().compareTo(mntVen) < 0) {
            throw new RuntimeException(String.format(
                    "Insufficient funds in %s: Available: %s, Required: %s",
                    soldPosition.getCurrency().getIdentifier(),
                    soldPosition.getMntDev(),
                    mntVen
            ));
        }
    }

    private void updatePositions(Position boughtPosition, Position soldPosition,
                                 BigDecimal mntAcht, BigDecimal mntVen,
                                 String transactionType) {
        boughtPosition.setMntDev(boughtPosition.getMntDev().add(mntAcht));
        soldPosition.setMntDev(soldPosition.getMntDev().subtract(mntVen));
//        updateDailyNeeds(boughtPosition, soldPosition, mntAcht, mntVen, transactionType);
    }

    private void updateDailyNeeds(Position boughtPosition, Position soldPosition,
                                  BigDecimal mntAcht, BigDecimal mntVen,
                                  String transactionType) {
        if ("BUY".equalsIgnoreCase(transactionType)) {
            updatePositionNeeds(boughtPosition, mntAcht);
        } else {
            updatePositionNeeds(soldPosition, mntVen);
        }
    }

    private void updatePositionNeeds(Position position, BigDecimal amount) {
        BigDecimal newBesoin = position.getBesoinDev().subtract(amount);
        position.setBesoinDev(newBesoin.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : newBesoin);
    }

    private void updatePositionList(Position... positions) {
        for (Position position : positions) {
            positionList.removeIf(p -> p.getPk().equals(position.getPk()));
            positionList.add(position);
        }
        logger.debug("Updated positionList: {}", positionList);
    }

    @Transactional
    public void setDailyNeeds(Integer currencyId, BigDecimal besoinDev) {
        Currency currency = getCurrency(currencyId, "");
        Position position = getOrCreatePosition(currency);
        position.setBesoinDev(besoinDev != null ? besoinDev : BigDecimal.ZERO);
        positionRepository.save(position);
        updatePositionList(position, position); // Mettre à jour la liste avec la position modifiée
        broadcastPositions();
    }

    public BigDecimal getPositionValue(Integer currencyId) {
        Currency currency = getCurrency(currencyId, "");
        return positionRepository.findByCurrency(currency)
                .map(Position::getMntDev)
                .orElse(BigDecimal.ZERO);
    }

    public List<Position> getPositionsWithNeeds() {
        return positionRepository.findAll().stream()
                .filter(p -> p.getBesoinDev().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());
    }

    public List<Position> getAllPositions() {
        return positionList;
    }

    private void broadcastPositions() {
        try {
            List<PositionDTO> positionDTOs = positionList.stream().map(this::toDTO).collect(Collectors.toList());
            socketIOServer.getBroadcastOperations().sendEvent("positionsUpdate", positionDTOs);
            logger.debug("Broadcasted {} positions", positionDTOs.size());
        } catch (Exception e) {
            logger.error("Error broadcasting positions", e);
        }
    }

    // Conversion Entité -> DTO
    private PositionDTO toDTO(Position position) {
        PositionDTO dto = new PositionDTO();
        dto.setPk(position.getPk());
        dto.setIdentifier(position.getCurrency().getIdentifier());
        dto.setMntDev(position.getMntDev());
        dto.setBesoinDev(position.getBesoinDev());
        return dto;
    }

    // Conversion DTO -> Entité
    private Position toEntity(PositionDTO dto) {
        Position position = new Position();
        position.setPk(dto.getPk());
        Currency currency = currencyRepository.findByIdentifier(dto.getIdentifier())
                .orElseThrow(() -> new IllegalArgumentException("Devise introuvable pour l'identifiant : " + dto.getIdentifier()));
        position.setCurrency(currency);
        position.setMntDev(dto.getMntDev() != null ? dto.getMntDev() : BigDecimal.ZERO);
        position.setBesoinDev(dto.getBesoinDev() != null ? dto.getBesoinDev() : BigDecimal.ZERO);
        return position;
    }

    public Optional<PositionDTO> getPositionById(Integer id) {
        return positionRepository.findById(id).map(this::toDTO);
    }

    public PositionDTO createPosition(PositionDTO positionDTO) {
        Currency currency = currencyRepository.findByIdentifier(positionDTO.getIdentifier())
                .orElseThrow(() -> new IllegalArgumentException("Devise introuvable pour l'identifiant : " + positionDTO.getIdentifier()));
        if (positionRepository.existsByCurrencyId(currency.getPk())) {
            throw new IllegalStateException("Cette devise a déjà une position associée");
        }
        Position position = toEntity(positionDTO);
        Position savedPosition = positionRepository.save(position);
        updatePositionList(savedPosition, savedPosition); // Mettre à jour la liste
        broadcastPositions();
        return toDTO(savedPosition);
    }

    public List<PositionDTO> createPositions(List<PositionDTO> positionDTOs) {
        List<Position> savedPositions = positionDTOs.stream().map(dto -> {
            Currency currency = currencyRepository.findByIdentifier(dto.getIdentifier())
                    .orElseThrow(() -> new IllegalArgumentException("Devise introuvable pour l'identifiant : " + dto.getIdentifier()));
            if (positionRepository.existsByCurrencyId(currency.getPk())) {
                throw new IllegalStateException("La devise avec l'identifiant " + dto.getIdentifier() + " a déjà une position associée");
            }
            Position position = toEntity(dto);
            return positionRepository.save(position);
        }).collect(Collectors.toList());

        savedPositions.forEach(position -> updatePositionList(position, position));
        broadcastPositions();
        return savedPositions.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Optional<PositionDTO> updatePosition(Integer id, PositionDTO positionDTO) {
        return positionRepository.findById(id).map(position -> {
            Currency newCurrency = currencyRepository.findByIdentifier(positionDTO.getIdentifier())
                    .orElseThrow(() -> new IllegalArgumentException("Devise introuvable pour l'identifiant : " + positionDTO.getIdentifier()));
            if (!position.getCurrency().getPk().equals(newCurrency.getPk()) &&
                    positionRepository.existsByCurrencyId(newCurrency.getPk())) {
                throw new IllegalStateException("La nouvelle devise a déjà une position associée");
            }
            position.setCurrency(newCurrency);
            position.setMntDev(positionDTO.getMntDev() != null ? positionDTO.getMntDev() : BigDecimal.ZERO);
            position.setBesoinDev(positionDTO.getBesoinDev() != null ? positionDTO.getBesoinDev() : BigDecimal.ZERO);
            Position updatedPosition = positionRepository.save(position);
            updatePositionList(updatedPosition, updatedPosition);
            broadcastPositions();
            return toDTO(updatedPosition);
        });
    }

    public boolean deletePosition(Integer id) {
        if (positionRepository.existsById(id)) {
            positionRepository.deleteById(id);
            positionList.removeIf(p -> p.getPk().equals(id));
            broadcastPositions();
            return true;
        }
        return false;
    }
}