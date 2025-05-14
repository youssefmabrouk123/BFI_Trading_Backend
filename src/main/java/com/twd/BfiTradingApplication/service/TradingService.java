//package com.twd.BfiTradingApplication.service;
//
//import com.corundumstudio.socketio.SocketIOServer;
//import com.twd.BfiTradingApplication.dto.PositionDTO;
//import com.twd.BfiTradingApplication.entity.*;
//import com.twd.BfiTradingApplication.exception.InsufficientFundsException;
//import com.twd.BfiTradingApplication.exception.InvalidCurrencyException;
//import com.twd.BfiTradingApplication.exception.TradingException;
//import com.twd.BfiTradingApplication.repository.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import jakarta.annotation.PostConstruct;
//import org.w3c.dom.css.Counter;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//public class TradingService {
//    private static final Logger logger = LoggerFactory.getLogger(TradingService.class);
//    @Autowired
//    private CrossParityRepository crossParityRepository;
//    private final TransactionRepository transactionRepository;
//    private final PositionRepository positionRepository;
//    private final CurrencyRepository currencyRepository;
//    private final SocketIOServer socketIOServer;
//    private final UserActionRepository userActionRepository;
//    private final UserRepository userRepository;
//
//    @Autowired
//    CounterpartyRepository counterpartyRepository;
//
//    private List<Position> positionList; // Liste en mémoire pour les positions
//
//    @Autowired
//    public TradingService(
//            TransactionRepository transactionRepository,
//            PositionRepository positionRepository,
//            CurrencyRepository currencyRepository,
//            UserRepository userRepository,
//            UserActionRepository userActionRepository,
//            SocketIOServer socketIOServer) {
//        this.transactionRepository = transactionRepository;
//        this.positionRepository = positionRepository;
//        this.currencyRepository = currencyRepository;
//        this.userActionRepository = userActionRepository;
//        this.socketIOServer = socketIOServer;
//        this.userRepository = userRepository;
//    }
//
//    @PostConstruct
//    public void init() {
//        positionList = positionRepository.findAll();
//        logger.info("Initial positions loaded: {}", positionList.size());
//        broadcastPositions();
//
//        socketIOServer.addConnectListener(client -> {
//            logger.info("Client connected: {}", client.getSessionId());
//            client.sendEvent("positionsUpdate", positionList.stream().map(this::toDTO).collect(Collectors.toList()));
//        });
//
//        socketIOServer.addEventListener("requestPositions", String.class, (client, data, ackSender) -> {
//            logger.info("Positions requested by client: {}", client.getSessionId());
//            client.sendEvent("positionsUpdate", positionList.stream().map(this::toDTO).collect(Collectors.toList()));
//        });
//    }
//
//    public CrossParity getCrossParityByCurrencies(Currency baseCurrency, Currency quoteCurrency) {
//        return crossParityRepository.findByBaseCurrencyAndQuoteCurrency(baseCurrency, quoteCurrency)
//                .orElseThrow(() -> new TradingException("CrossParity not found for " + baseCurrency.getIdentifier() + "/" + quoteCurrency.getIdentifier()));
//    }
//
//    @Transactional
//    public Transaction executeTrade(Integer userId, String crossParityName,
//                                    BigDecimal mntAcht,BigDecimal mntVen, String transactionType, BigDecimal marketPrice,Integer counterpartyId) {
//        CrossParity crossParity = crossParityRepository.findByIdentifier(crossParityName);
//        Integer baseCurrencyId = crossParity.getBaseCurrency().getPk();
//        Integer quoteCurrencyId = crossParity.getQuoteCurrency().getPk();
//
//        logger.debug("Executing trade for userId={}, baseCurrencyId={}, quoteCurrencyId={}, amount={}, type={}, price={}",
//                userId, baseCurrencyId, quoteCurrencyId, mntAcht, transactionType, marketPrice);
//
//        try {
//            validateTradeParameters(baseCurrencyId, quoteCurrencyId, mntAcht, transactionType, marketPrice);
//            User user = userRepository.findById(userId)
//                    .orElseThrow(() -> new TradingException("User not found with ID: " + userId));
//
//            Counterparty counterparty = counterpartyRepository.findById(counterpartyId)
//                    .orElseThrow(() -> new TradingException("Counterparty not found with ID: " + counterpartyId));
//
//            Currency devAchn = getCurrency(baseCurrencyId, "Base");
//            Currency devVen = getCurrency(quoteCurrencyId, "Quote");
//
//            //BigDecimal quotity = getCrossParityByCurrencies(devAchn, devVen).getQuotity();
//
//            //BigDecimal mntVen = calculateSoldAmount(mntAcht, marketPrice, quotity);
//
//            TradeCurrencyDetails tradeDetails = determineTradeCurrencyDetails(
//                    devAchn, devVen, mntAcht, mntVen, transactionType
//            );
//
//            Position boughtPosition = getOrCreatePosition(tradeDetails.boughtCurrency, user);
//            Position soldPosition = getOrCreatePosition(tradeDetails.soldCurrency, user);
//
//            validateFunds(soldPosition, tradeDetails.finalMntVen, transactionType);
//
//            Transaction transaction = createTransaction(
//                    tradeDetails.boughtCurrency,
//                    tradeDetails.soldCurrency,
//                    tradeDetails.finalMntAcht,
//                    tradeDetails.finalMntVen,
//                    marketPrice,
//                    transactionType,
//                    user,
//                    counterparty
//
//
//            );
//
//            updatePositions(boughtPosition, soldPosition, tradeDetails.finalMntAcht, tradeDetails.finalMntVen, transactionType);
//
//            transactionRepository.save(transaction);
//            positionRepository.save(boughtPosition);
//            positionRepository.save(soldPosition);
//
//            UserAction userAction = new UserAction();
//            userAction.setUser(user);
//            userAction.setActionType("TRANSACTION");
//            userAction.setDetails(String.format("%s %s of %s for %s %s", transactionType, mntAcht,
//                    devAchn.getIdentifier(), mntVen, devVen.getIdentifier()));
//            userAction.setAmount(tradeDetails.finalMntAcht);
//            userAction.setCurrency(tradeDetails.boughtCurrency);
//            userAction.setActionTime(LocalDateTime.now());
//            userActionRepository.save(userAction);
//
//            updatePositionList(boughtPosition, soldPosition);
//            broadcastPositions();
//
//            logger.info("Trade successful: {} {} of {} at market price {} for {} {}",
//                    transactionType,
//                    tradeDetails.finalMntAcht,
//                    tradeDetails.boughtCurrency.getIdentifier(),
//                    marketPrice,
//                    tradeDetails.finalMntVen,
//                    tradeDetails.soldCurrency.getIdentifier()
//            );
//
//            return transaction;
//
//        } catch (InsufficientFundsException e) {
//            throw new TradingException("Insufficient funds to execute the trade: " + e.getMessage());
//        } catch (InvalidCurrencyException e) {
//            throw new TradingException("Invalid currency identifiers provided: " + e.getMessage());
//        } catch (Exception e) {
//            throw new TradingException("Unexpected error during trade execution: " + e.getMessage());
//        }
//    }
//
//    private void validateTradeParameters(Integer baseCurrencyId, Integer quoteCurrencyId,
//                                         BigDecimal mntAcht, String transactionType,
//                                         BigDecimal marketPrice) {
//        if (baseCurrencyId == null || quoteCurrencyId == null) {
//            throw new IllegalArgumentException("Currency IDs cannot be null");
//        }
//        if (mntAcht == null || mntAcht.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new IllegalArgumentException("Invalid trade amount");
//        }
//        if (marketPrice == null || marketPrice.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new IllegalArgumentException("Invalid market price");
//        }
//        if (!"BUY".equalsIgnoreCase(transactionType) && !"SELL".equalsIgnoreCase(transactionType)) {
//            throw new IllegalArgumentException("Invalid transaction type. Must be BUY or SELL.");
//        }
//    }
//
//    private Currency getCurrency(Integer currencyId, String type) {
//        return currencyRepository.findById(currencyId)
//                .orElseThrow(() -> new TradingException(type + " currency not found with ID: " + currencyId));
//    }
//
//    private BigDecimal calculateSoldAmount(BigDecimal mntAcht, BigDecimal marketPrice, BigDecimal quotity) {
//        return mntAcht.multiply(marketPrice)
//                .setScale(4, RoundingMode.HALF_UP)
//                .divide(quotity, 4, RoundingMode.HALF_UP);
//    }
//
//    private static class TradeCurrencyDetails {
//        Currency boughtCurrency;
//        Currency soldCurrency;
//        BigDecimal finalMntAcht;
//        BigDecimal finalMntVen;
//
//        TradeCurrencyDetails(Currency boughtCurrency, Currency soldCurrency,
//                             BigDecimal finalMntAcht, BigDecimal finalMntVen) {
//            this.boughtCurrency = boughtCurrency;
//            this.soldCurrency = soldCurrency;
//            this.finalMntAcht = finalMntAcht;
//            this.finalMntVen = finalMntVen;
//        }
//    }
//
//    private TradeCurrencyDetails determineTradeCurrencyDetails(
//            Currency devAchn, Currency devVen,
//            BigDecimal mntAcht, BigDecimal mntVen,
//            String transactionType) {
//        boolean isBuy = "BUY".equalsIgnoreCase(transactionType);
//        Currency boughtCurrency = isBuy ? devAchn : devVen;
//        Currency soldCurrency = isBuy ? devVen : devAchn;
//        BigDecimal finalMntAcht = isBuy ? mntAcht : mntVen;
//        BigDecimal finalMntVen = isBuy ? mntVen : mntAcht;
//        return new TradeCurrencyDetails(boughtCurrency, soldCurrency, finalMntAcht, finalMntVen);
//    }
//
//    private Transaction createTransaction(Currency boughtCurrency, Currency soldCurrency,
//                                          BigDecimal finalMntAcht, BigDecimal finalMntVen,
//                                          BigDecimal marketPrice, String transactionType, User user,Counterparty counterparty) {
//        return new Transaction(boughtCurrency, soldCurrency, finalMntAcht, finalMntVen, marketPrice, transactionType.toUpperCase(), user, counterparty);
//    }
//
//    private Position getOrCreatePosition(Currency currency, User user) {
//        return positionRepository.findByCurrency(currency)
//                .orElseGet(() -> {
//                    Position newPosition = new Position(currency, BigDecimal.ZERO, BigDecimal.ZERO, user);
//                    return positionRepository.save(newPosition);
//                });
//    }
//
//    private void validateFunds(Position soldPosition, BigDecimal mntVen, String transactionType) {
//        if (soldPosition.getMntDev().compareTo(mntVen) < 0) {
//
//            throw new InsufficientFundsException(String.format(
//                    "Insufficient funds in %s: Available: %s, Required: %s",
//                    soldPosition.getCurrency().getIdentifier(),
//                    soldPosition.getMntDev(),
//                    mntVen
//            ));
//        }
//    }
//
//    private void updatePositions(Position boughtPosition, Position soldPosition,
//                                 BigDecimal mntAcht, BigDecimal mntVen,
//                                 String transactionType) {
//        boughtPosition.setMntDev(boughtPosition.getMntDev().add(mntAcht));
//        soldPosition.setMntDev(soldPosition.getMntDev().subtract(mntVen));
//    }
//
//    private void updatePositionList(Position... positions) {
//        for (Position position : positions) {
//            positionList.removeIf(p -> p.getPk().equals(position.getPk()));
//            positionList.add(position);
//        }
//        logger.debug("Updated positionList: {}", positionList);
//    }
//
//    @Transactional
//    public void setDailyNeeds(Integer currencyId, BigDecimal besoinDev, User user) {
//        Currency currency = getCurrency(currencyId, "");
//        Position position = getOrCreatePosition(currency, user);
//        position.setBesoinDev(besoinDev != null ? besoinDev : BigDecimal.ZERO);
//        positionRepository.save(position);
//        updatePositionList(position);
//        broadcastPositions();
//    }
//
//    public BigDecimal getPositionValue(Integer currencyId) {
//        Currency currency = getCurrency(currencyId, "");
//        return positionRepository.findByCurrency(currency)
//                .map(Position::getMntDev)
//                .orElse(BigDecimal.ZERO);
//    }
//
//    public List<Position> getPositionsWithNeeds() {
//        return positionRepository.findAll().stream()
//                .filter(p -> p.getBesoinDev().compareTo(BigDecimal.ZERO) > 0)
//                .collect(Collectors.toList());
//    }
//
//    public List<Position> getAllPositions() {
//        return positionList;
//    }
//
//    private void broadcastPositions() {
//        try {
//            List<PositionDTO> positionDTOs = positionList.stream().map(this::toDTO).collect(Collectors.toList());
//            socketIOServer.getBroadcastOperations().sendEvent("positionsUpdate", positionDTOs);
//            logger.debug("Broadcasted {} positions", positionDTOs.size());
//        } catch (Exception e) {
//            logger.error("Error broadcasting positions", e);
//        }
//    }
//
//    private PositionDTO toDTO(Position position) {
//        PositionDTO dto = new PositionDTO();
//        dto.setPk(position.getPk());
//        dto.setIdentifier(position.getCurrency().getIdentifier());
//        dto.setMntDev(position.getMntDev());
//        dto.setBesoinDev(position.getBesoinDev());
//        return dto;
//    }
//
//    private Position toEntity(PositionDTO dto) {
//        Position position = new Position();
//        position.setPk(dto.getPk());
//        Currency currency = currencyRepository.findByIdentifier(dto.getIdentifier())
//                .orElseThrow(() -> new IllegalArgumentException("Currency not found for identifier: " + dto.getIdentifier()));
//        position.setCurrency(currency);
//        position.setMntDev(dto.getMntDev() != null ? dto.getMntDev() : BigDecimal.ZERO);
//        position.setBesoinDev(dto.getBesoinDev() != null ? dto.getBesoinDev() : BigDecimal.ZERO);
//        return position;
//    }
//
//    public Optional<PositionDTO> getPositionById(Integer id) {
//        return positionRepository.findById(id).map(this::toDTO);
//    }
//
//    public PositionDTO createPosition(PositionDTO positionDTO) {
//        Currency currency = currencyRepository.findByIdentifier(positionDTO.getIdentifier())
//                .orElseThrow(() -> new IllegalArgumentException("Currency not found for identifier: " + positionDTO.getIdentifier()));
//        if (positionRepository.existsByCurrencyId(currency.getPk())) {
//            throw new IllegalStateException("This currency already has an associated position");
//        }
//        Position position = toEntity(positionDTO);
//        Position savedPosition = positionRepository.save(position);
//        updatePositionList(savedPosition);
//        broadcastPositions();
//        return toDTO(savedPosition);
//    }
//
//    public List<PositionDTO> createPositions(List<PositionDTO> positionDTOs, User user) {
//        List<Position> savedPositions = positionDTOs.stream().map(dto -> {
//            Currency currency = currencyRepository.findByIdentifier(dto.getIdentifier())
//                    .orElseThrow(() -> new IllegalArgumentException("Currency not found for identifier: " + dto.getIdentifier()));
//            if (positionRepository.existsByCurrencyId(currency.getPk())) {
//                throw new IllegalStateException("Currency with identifier " + dto.getIdentifier() + " already has an associated position");
//            }
//            Position position = toEntity(dto);
//            position.setUser(user);
//            return positionRepository.save(position);
//        }).collect(Collectors.toList());
//
//        savedPositions.forEach(position -> updatePositionList(position));
//        broadcastPositions();
//        return savedPositions.stream().map(this::toDTO).collect(Collectors.toList());
//    }
//
//    public Optional<PositionDTO> updatePosition(Integer id, PositionDTO positionDTO) {
//        return positionRepository.findById(id).map(position -> {
//            Currency newCurrency = currencyRepository.findByIdentifier(positionDTO.getIdentifier())
//                    .orElseThrow(() -> new IllegalArgumentException("Currency not found for identifier: " + positionDTO.getIdentifier()));
//            if (!position.getCurrency().getPk().equals(newCurrency.getPk()) &&
//                    positionRepository.existsByCurrencyId(newCurrency.getPk())) {
//                throw new IllegalStateException("The new currency already has an associated position");
//            }
//            position.setCurrency(newCurrency);
//            position.setMntDev(positionDTO.getMntDev() != null ? positionDTO.getMntDev() : BigDecimal.ZERO);
//            position.setBesoinDev(positionDTO.getBesoinDev() != null ? positionDTO.getBesoinDev() : BigDecimal.ZERO);
//            Position updatedPosition = positionRepository.save(position);
//            updatePositionList(updatedPosition);
//            broadcastPositions();
//            return toDTO(updatedPosition);
//        });
//    }
//
//    public boolean deletePosition(Integer id) {
//        if (positionRepository.existsById(id)) {
//            positionRepository.deleteById(id);
//            positionList.removeIf(p -> p.getPk().equals(id));
//            broadcastPositions();
//            return true;
//        }
//        return false;
//    }
//
//    public User getUserByEmail(String email) {
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> new TradingException("User not found with email: " + email));
//    }
//
//    public List<Position> getPositionsByUserId(Integer userId) {
//        return positionRepository.findByUserId(userId);
//    }
//}
//
//
//
//
//
////
////package com.twd.BfiTradingApplication.service;
////import com.corundumstudio.socketio.SocketIOServer;
////import com.twd.BfiTradingApplication.dto.PositionDTO;
////import com.twd.BfiTradingApplication.entity.*;
////import com.twd.BfiTradingApplication.exception.InsufficientFundsException;
////import com.twd.BfiTradingApplication.exception.InvalidCurrencyException;
////import com.twd.BfiTradingApplication.exception.TradingException;
////import com.twd.BfiTradingApplication.repository.*;
////import org.slf4j.Logger;
////import org.slf4j.LoggerFactory;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.stereotype.Service;
////import org.springframework.transaction.annotation.Transactional;
////
////import jakarta.annotation.PostConstruct;
////import java.math.BigDecimal;
////import java.math.RoundingMode;
////import java.time.LocalDateTime;
////import java.util.List;
////import java.util.Optional;
////import java.util.stream.Collectors;
////
////@Service
////public class TradingService {
////    private static final Logger logger = LoggerFactory.getLogger(TradingService.class);
////
////    @Autowired
////    private CrossParityRepository crossParityRepository;
////
////    @Autowired
////    private TransactionRepository transactionRepository;
////
////    @Autowired
////    private PositionRepository positionRepository;
////
////    @Autowired
////    private CurrencyRepository currencyRepository;
////
////    @Autowired
////    private CounterpartyRepository counterpartyRepository;
////
////    @Autowired
////    private SocketIOServer socketIOServer;
////
////    @Autowired
////    private UserActionRepository userActionRepository;
////
////    @Autowired
////    private UserRepository userRepository;
////
////    private List<Position> positionList;
////
////    @Autowired
////    public TradingService(
////            TransactionRepository transactionRepository,
////            PositionRepository positionRepository,
////            CurrencyRepository currencyRepository,
////            CounterpartyRepository counterpartyRepository,
////            UserRepository userRepository,
////            UserActionRepository userActionRepository,
////            SocketIOServer socketIOServer) {
////        this.transactionRepository = transactionRepository;
////        this.positionRepository = positionRepository;
////        this.currencyRepository = currencyRepository;
////        this.counterpartyRepository = counterpartyRepository;
////        this.userRepository = userRepository;
////        this.userActionRepository = userActionRepository;
////        this.socketIOServer = socketIOServer;
////    }
////
////    @PostConstruct
////    public void init() {
////        positionList = positionRepository.findAll();
////        logger.info("Initial positions loaded: {}", positionList.size());
////        broadcastPositions();
////
////        socketIOServer.addConnectListener(client -> {
////            logger.info("Client connected: {}", client.getSessionId());
////            client.sendEvent("positionsUpdate", positionList.stream().map(this::toDTO).collect(Collectors.toList()));
////        });
////
////        socketIOServer.addEventListener("requestPositions", String.class, (client, data, ackSender) -> {
////            logger.info("Positions requested by client: {}", client.getSessionId());
////            client.sendEvent("positionsUpdate", positionList.stream().map(this::toDTO).collect(Collectors.toList()));
////        });
////    }
////
////    public CrossParity getCrossParityByCurrencies(Currency baseCurrency, Currency quoteCurrency) {
////        return crossParityRepository.findByBaseCurrencyAndQuoteCurrency(baseCurrency, quoteCurrency)
////                .orElseThrow(() -> new TradingException("CrossParity not found for " + baseCurrency.getIdentifier() + "/" + quoteCurrency.getIdentifier()));
////    }
////
////    @Transactional
////    public Transaction executeTrade(Integer userId, Integer baseCurrencyId, Integer quoteCurrencyId,
////                                    BigDecimal mntAcht, String transactionType, BigDecimal marketPrice,
////                                    Integer counterpartyId) {
////        logger.debug("Executing trade for userId={}, baseCurrencyId={}, quoteCurrencyId={}, amount={}, type={}, price={}, counterpartyId={}",
////                userId, baseCurrencyId, quoteCurrencyId, mntAcht, transactionType, marketPrice, counterpartyId);
////
////        try {
////            validateTradeParameters(baseCurrencyId, quoteCurrencyId, mntAcht, transactionType, marketPrice, counterpartyId);
////            User user = userRepository.findById(userId)
////                    .orElseThrow(() -> new TradingException("User not found with ID: " + userId));
////            Counterparty counterparty = counterpartyRepository.findById(counterpartyId)
////                    .orElseThrow(() -> new TradingException("Counterparty not found with ID: " + counterpartyId));
////
////            Currency devAchn = getCurrency(baseCurrencyId, "Base");
////            Currency devVen = getCurrency(quoteCurrencyId, "Quote");
////
////            BigDecimal quotity = getCrossParityByCurrencies(devAchn, devVen).getQuotity();
////
////            BigDecimal mntVen = calculateSoldAmount(mntAcht, marketPrice, quotity);
////
////            TradeCurrencyDetails tradeDetails = determineTradeCurrencyDetails(
////                    devAchn, devVen, mntAcht, mntVen, transactionType
////            );
////
////            Position boughtPosition = getOrCreatePosition(tradeDetails.boughtCurrency, user);
////            Position soldPosition = getOrCreatePosition(tradeDetails.soldCurrency, user);
////
////            validateFunds(soldPosition, tradeDetails.finalMntVen, transactionType);
////
////            Transaction transaction = createTransaction(
////                    tradeDetails.boughtCurrency,
////                    tradeDetails.soldCurrency,
////                    tradeDetails.finalMntAcht,
////                    tradeDetails.finalMntVen,
////                    marketPrice,
////                    transactionType,
////                    user,
////                    counterparty
////            );
////
////            updatePositions(boughtPosition, soldPosition, tradeDetails.finalMntAcht, tradeDetails.finalMntVen, transactionType);
////
////            transactionRepository.save(transaction);
////            positionRepository.save(boughtPosition);
////            positionRepository.save(soldPosition);
////
////            UserAction userAction = new UserAction();
////            userAction.setUser(user);
////            userAction.setActionType("TRANSACTION");
////            userAction.setDetails(String.format("%s %s of %s for %s %s with counterparty %s",
////                    transactionType, mntAcht, devAchn.getIdentifier(), mntVen, devVen.getIdentifier(), counterparty.getName()));
////            userAction.setAmount(tradeDetails.finalMntAcht);
////            userAction.setCurrency(tradeDetails.boughtCurrency);
////            userAction.setActionTime(LocalDateTime.now());
////            userActionRepository.save(userAction);
////
////            updatePositionList(boughtPosition, soldPosition);
////            broadcastPositions();
////
////            logger.info("Trade successful: {} {} of {} at market price {} for {} {} with counterparty {}",
////                    transactionType,
////                    tradeDetails.finalMntAcht,
////                    tradeDetails.boughtCurrency.getIdentifier(),
////                    marketPrice,
////                    tradeDetails.finalMntVen,
////                    tradeDetails.soldCurrency.getIdentifier(),
////                    counterparty.getName()
////            );
////
////            return transaction;
////
////        } catch (InsufficientFundsException e) {
////            throw new TradingException("Insufficient funds to execute the trade: " + e.getMessage());
////        } catch (InvalidCurrencyException e) {
////            throw new TradingException("Invalid currency identifiers provided: " + e.getMessage());
////        } catch (Exception e) {
////            throw new TradingException("Unexpected error during trade execution: " + e.getMessage());
////        }
////    }
////
////    private void validateTradeParameters(Integer baseCurrencyId, Integer quoteCurrencyId,
////                                         BigDecimal mntAcht, String transactionType,
////                                         BigDecimal marketPrice, Integer counterpartyId) {
////        if (baseCurrencyId == null || quoteCurrencyId == null || counterpartyId == null) {
////            throw new IllegalArgumentException("Currency IDs and counterparty ID cannot be null");
////        }
////        if (mntAcht == null || mntAcht.compareTo(BigDecimal.ZERO) <= 0) {
////            throw new IllegalArgumentException("Invalid trade amount");
////        }
////        if (marketPrice == null || marketPrice.compareTo(BigDecimal.ZERO) <= 0) {
////            throw new IllegalArgumentException("Invalid market price");
////        }
////        if (!"BUY".equalsIgnoreCase(transactionType) && !"SELL".equalsIgnoreCase(transactionType)) {
////            throw new IllegalArgumentException("Invalid transaction type. Must be BUY or SELL.");
////        }
////    }
////
////    private Currency getCurrency(Integer currencyId, String type) {
////        return currencyRepository.findById(currencyId)
////                .orElseThrow(() -> new TradingException(type + " currency not found with ID: " + currencyId));
////    }
////
////    private BigDecimal calculateSoldAmount(BigDecimal mntAcht, BigDecimal marketPrice, BigDecimal quotity) {
////        return mntAcht.multiply(marketPrice)
////                .divide(quotity, 4, RoundingMode.HALF_UP);
////    }
////
////    private static class TradeCurrencyDetails {
////        Currency boughtCurrency;
////        Currency soldCurrency;
////        BigDecimal finalMntAcht;
////        BigDecimal finalMntVen;
////
////        TradeCurrencyDetails(Currency boughtCurrency, Currency soldCurrency,
////                             BigDecimal finalMntAcht, BigDecimal finalMntVen) {
////            this.boughtCurrency = boughtCurrency;
////            this.soldCurrency = soldCurrency;
////            this.finalMntAcht = finalMntAcht;
////            this.finalMntVen = finalMntVen;
////        }
////    }
////
////    private TradeCurrencyDetails determineTradeCurrencyDetails(
////            Currency devAchn, Currency devVen,
////            BigDecimal mntAcht, BigDecimal mntVen,
////            String transactionType) {
////        boolean isBuy = "BUY".equalsIgnoreCase(transactionType);
////        Currency boughtCurrency = isBuy ? devAchn : devVen;
////        Currency soldCurrency = isBuy ? devVen : devAchn;
////        BigDecimal finalMntAcht = isBuy ? mntAcht : mntVen;
////        BigDecimal finalMntVen = isBuy ? mntVen : mntAcht;
////        return new TradeCurrencyDetails(boughtCurrency, soldCurrency, finalMntAcht, finalMntVen);
////    }
////
////    private Transaction createTransaction(Currency boughtCurrency, Currency soldCurrency,
////                                          BigDecimal finalMntAcht, BigDecimal finalMntVen,
////                                          BigDecimal marketPrice, String transactionType,
////                                          User user, Counterparty counterparty) {
////        return new Transaction(boughtCurrency, soldCurrency, finalMntAcht, finalMntVen,
////                marketPrice, transactionType.toUpperCase(), user, counterparty);
////    }
////
////    private Position getOrCreatePosition(Currency currency, User user) {
////        return positionRepository.findByCurrency(currency)
////                .orElseGet(() -> {
////                    Position newPosition = new Position(currency, BigDecimal.ZERO, BigDecimal.ZERO, user);
////                    return positionRepository.save(newPosition);
////                });
////    }
////
////    private void validateFunds(Position soldPosition, BigDecimal mntVen, String transactionType) {
////        if (soldPosition.getMntDev().compareTo(mntVen) < 0) {
////            throw new InsufficientFundsException(String.format(
////                    "Insufficient funds in %s: Available: %s, Required: %s",
////                    soldPosition.getCurrency().getIdentifier(),
////                    soldPosition.getMntDev(),
////                    mntVen
////            ));
////        }
////    }
////
////    private void updatePositions(Position boughtPosition, Position soldPosition,
////                                 BigDecimal mntAcht, BigDecimal mntVen,
////                                 String transactionType) {
////        boughtPosition.setMntDev(boughtPosition.getMntDev().add(mntAcht));
////        soldPosition.setMntDev(soldPosition.getMntDev().subtract(mntVen));
////    }
////
////    private void updatePositionList(Position... positions) {
////        for (Position position : positions) {
////            positionList.removeIf(p -> p.getPk().equals(position.getPk()));
////            positionList.add(position);
////        }
////        logger.debug("Updated positionList: {}", positionList);
////    }
////
////    @Transactional
////    public void setDailyNeeds(Integer currencyId, BigDecimal besoinDev, User user) {
////        Currency currency = getCurrency(currencyId, "");
////        Position position = getOrCreatePosition(currency, user);
////        position.setBesoinDev(besoinDev != null ? besoinDev : BigDecimal.ZERO);
////        positionRepository.save(position);
////        updatePositionList(position);
////        broadcastPositions();
////    }
////
////    public BigDecimal getPositionValue(Integer currencyId) {
////        Currency currency = getCurrency(currencyId, "");
////        return positionRepository.findByCurrency(currency)
////                .map(Position::getMntDev)
////                .orElse(BigDecimal.ZERO);
////    }
////
////    public List<Position> getPositionsWithNeeds() {
////        return positionRepository.findAll().stream()
////                .filter(p -> p.getBesoinDev().compareTo(BigDecimal.ZERO) > 0)
////                .collect(Collectors.toList());
////    }
////
////    public List<Position> getAllPositions() {
////        return positionList;
////    }
////
////    private void broadcastPositions() {
////        try {
////            List<PositionDTO> positionDTOs = positionList.stream().map(this::toDTO).collect(Collectors.toList());
////            socketIOServer.getBroadcastOperations().sendEvent("positionsUpdate", positionDTOs);
////            logger.debug("Broadcasted {} positions", positionDTOs.size());
////        } catch (Exception e) {
////            logger.error("Error broadcasting positions", e);
////        }
////    }
////
////    private PositionDTO toDTO(Position position) {
////        PositionDTO dto = new PositionDTO();
////        dto.setPk(position.getPk());
////        dto.setIdentifier(position.getCurrency().getIdentifier());
////        dto.setMntDev(position.getMntDev());
////        dto.setBesoinDev(position.getBesoinDev());
////        return dto;
////    }
////
////    private Position toEntity(PositionDTO dto) {
////        Position position = new Position();
////        position.setPk(dto.getPk());
////        Currency currency = currencyRepository.findByIdentifier(dto.getIdentifier())
////                .orElseThrow(() -> new IllegalArgumentException("Currency not found for identifier: " + dto.getIdentifier()));
////        position.setCurrency(currency);
////        position.setMntDev(dto.getMntDev() != null ? dto.getMntDev() : BigDecimal.ZERO);
////        position.setBesoinDev(dto.getBesoinDev() != null ? dto.getBesoinDev() : BigDecimal.ZERO);
////        return position;
////    }
////
////    public Optional<PositionDTO> getPositionById(Integer id) {
////        return positionRepository.findById(id).map(this::toDTO);
////    }
////
////    public PositionDTO createPosition(PositionDTO positionDTO) {
////        Currency currency = currencyRepository.findByIdentifier(positionDTO.getIdentifier())
////                .orElseThrow(() -> new IllegalArgumentException("Currency not found for identifier: " + positionDTO.getIdentifier()));
////        if (positionRepository.existsByCurrencyId(currency.getPk())) {
////            throw new IllegalStateException("This currency already has an associated position");
////        }
////        Position position = toEntity(positionDTO);
////        Position savedPosition = positionRepository.save(position);
////        updatePositionList(savedPosition);
////        broadcastPositions();
////        return toDTO(savedPosition);
////    }
////
////    public List<PositionDTO> createPositions(List<PositionDTO> positionDTOs, User user) {
////        List<Position> savedPositions = positionDTOs.stream().map(dto -> {
////            Currency currency = currencyRepository.findByIdentifier(dto.getIdentifier())
////                    .orElseThrow(() -> new IllegalArgumentException("Currency not found for identifier: " + dto.getIdentifier()));
////            if (positionRepository.existsByCurrencyId(currency.getPk())) {
////                throw new IllegalStateException("Currency with identifier " + dto.getIdentifier() + " already has an associated position");
////            }
////            Position position = toEntity(dto);
////            position.setUser(user);
////            return positionRepository.save(position);
////        }).collect(Collectors.toList());
////
////        savedPositions.forEach(position -> updatePositionList(position));
////        broadcastPositions();
////        return savedPositions.stream().map(this::toDTO).collect(Collectors.toList());
////    }
////
////    public Optional<PositionDTO> updatePosition(Integer id, PositionDTO positionDTO) {
////        return positionRepository.findById(id).map(position -> {
////            Currency newCurrency = currencyRepository.findByIdentifier(positionDTO.getIdentifier())
////                    .orElseThrow(() -> new IllegalArgumentException("Currency not found for identifier: " + positionDTO.getIdentifier()));
////            if (!position.getCurrency().getPk().equals(newCurrency.getPk()) &&
////                    positionRepository.existsByCurrencyId(newCurrency.getPk())) {
////                throw new IllegalStateException("The new currency already has an associated position");
////            }
////            position.setCurrency(newCurrency);
////            position.setMntDev(positionDTO.getMntDev() != null ? positionDTO.getMntDev() : BigDecimal.ZERO);
////            position.setBesoinDev(positionDTO.getBesoinDev() != null ? positionDTO.getBesoinDev() : BigDecimal.ZERO);
////            Position updatedPosition = positionRepository.save(position);
////            updatePositionList(updatedPosition);
////            broadcastPositions();
////            return toDTO(updatedPosition);
////        });
////    }
////
////    public boolean deletePosition(Integer id) {
////        if (positionRepository.existsById(id)) {
////            positionRepository.deleteById(id);
////            positionList.removeIf(p -> p.getPk().equals(id));
////            broadcastPositions();
////            return true;
////        }
////        return false;
////    }
////
////    public User getUserByEmail(String email) {
////        return userRepository.findByEmail(email)
////                .orElseThrow(() -> new TradingException("User not found with email: " + email));
////    }
////
////    public List<Position> getPositionsByUserId(Integer userId) {
////        return positionRepository.findByUserId(userId);
////    }
////}




package com.twd.BfiTradingApplication.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.twd.BfiTradingApplication.dto.PositionDTO;
import com.twd.BfiTradingApplication.entity.*;
import com.twd.BfiTradingApplication.exception.InsufficientFundsException;
import com.twd.BfiTradingApplication.exception.InvalidCurrencyException;
import com.twd.BfiTradingApplication.exception.TradingException;
import com.twd.BfiTradingApplication.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TradingService {
    private static final Logger logger = LoggerFactory.getLogger(TradingService.class);

    @Autowired
    private CrossParityRepository crossParityRepository;

    private final TransactionRepository transactionRepository;
    private final PositionRepository positionRepository;
    private final CurrencyRepository currencyRepository;
    private final SocketIOServer socketIOServer;
    private final UserActionRepository userActionRepository;
    private final UserRepository userRepository;

    @Autowired
    CounterpartyRepository counterpartyRepository;

    private List<Position> positionList;

    @Autowired
    public TradingService(
            TransactionRepository transactionRepository,
            PositionRepository positionRepository,
            CurrencyRepository currencyRepository,
            UserRepository userRepository,
            UserActionRepository userActionRepository,
            SocketIOServer socketIOServer) {
        this.transactionRepository = transactionRepository;
        this.positionRepository = positionRepository;
        this.currencyRepository = currencyRepository;
        this.userActionRepository = userActionRepository;
        this.socketIOServer = socketIOServer;
        this.userRepository = userRepository;
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

    public CrossParity getCrossParityByCurrencies(Currency baseCurrency, Currency quoteCurrency) {
        return crossParityRepository.findByBaseCurrencyAndQuoteCurrency(baseCurrency, quoteCurrency)
                .orElseThrow(() -> new TradingException("CrossParity not found for " + baseCurrency.getIdentifier() + "/" + quoteCurrency.getIdentifier()));
    }

    @Transactional
    public Transaction executeTrade(Integer userId, String crossParityName,
                                    BigDecimal mntAcht, BigDecimal mntVen, String transactionType,
                                    BigDecimal marketPrice, Integer counterpartyId , LocalDate valueDate) {
        CrossParity crossParity = crossParityRepository.findByIdentifier(crossParityName);
        if (crossParity == null) {
            throw new TradingException("CrossParity not found: " + crossParityName);
        }
        Integer baseCurrencyId = crossParity.getBaseCurrency().getPk();
        Integer quoteCurrencyId = crossParity.getQuoteCurrency().getPk();

        logger.debug("Executing trade for userId={}, crossParity={}, mntAcht={}, mntVen={}, type={}, price={}, counterpartyId={}",
                userId, crossParityName, mntAcht, mntVen, transactionType, marketPrice, counterpartyId);

        try {
            validateTradeParameters(baseCurrencyId, quoteCurrencyId, mntAcht, mntVen, transactionType, marketPrice, counterpartyId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new TradingException("User not found with ID: " + userId));
            Counterparty counterparty = counterpartyRepository.findById(counterpartyId)
                    .orElseThrow(() -> new TradingException("Counterparty not found with ID: " + counterpartyId));

            Currency devAchn = getCurrency(baseCurrencyId, "Base");
            Currency devVen = getCurrency(quoteCurrencyId, "Quote");

            TradeCurrencyDetails tradeDetails = determineTradeCurrencyDetails(
                    devAchn, devVen, mntAcht, mntVen, transactionType
            );

            Position boughtPosition = getOrCreatePosition(tradeDetails.boughtCurrency, user);
            Position soldPosition = getOrCreatePosition(tradeDetails.soldCurrency, user);

            validateFunds(soldPosition, tradeDetails.finalMntVen, transactionType);

            Transaction transaction = createTransaction(
                    tradeDetails.boughtCurrency,
                    tradeDetails.soldCurrency,
                    tradeDetails.finalMntAcht,
                    tradeDetails.finalMntVen,
                    marketPrice,
                    transactionType,
                    user,
                    counterparty,
                    valueDate
            );

            updatePositions(boughtPosition, soldPosition, tradeDetails.finalMntAcht, tradeDetails.finalMntVen, transactionType);

            transactionRepository.save(transaction);
            positionRepository.save(boughtPosition);
            positionRepository.save(soldPosition);

            UserAction userAction = new UserAction();
            userAction.setUser(user);
            userAction.setActionType("TRANSACTION");
            userAction.setDetails(String.format("%s %s ofDefaulting to standard quote currency %s for %s %s with counterparty %s",
                    transactionType, tradeDetails.finalMntAcht, devAchn.getIdentifier(),
                    tradeDetails.finalMntVen, devVen.getIdentifier(), counterparty.getName()));
            userAction.setAmount(tradeDetails.finalMntAcht);
            userAction.setCurrency(tradeDetails.boughtCurrency);
            userAction.setActionTime(LocalDateTime.now());
            userActionRepository.save(userAction);

            updatePositionList(boughtPosition, soldPosition);
            broadcastPositions();

            logger.info("Trade successful: {} {} of {} at market price {} for {} {} with counterparty {}",
                    transactionType,
                    tradeDetails.finalMntAcht,
                    tradeDetails.boughtCurrency.getIdentifier(),
                    marketPrice,
                    tradeDetails.finalMntVen,
                    tradeDetails.soldCurrency.getIdentifier(),
                    counterparty.getName()
            );

            return transaction;

        } catch (InsufficientFundsException e) {
            throw new TradingException("Insufficient funds to execute the trade: " + e.getMessage());
        } catch (InvalidCurrencyException e) {
            throw new TradingException("Invalid currency identifiers provided: " + e.getMessage());
        } catch (Exception e) {
            throw new TradingException("Unexpected error during trade execution: " + e.getMessage());
        }
    }

    private void validateTradeParameters(Integer baseCurrencyId, Integer quoteCurrencyId,
                                         BigDecimal mntAcht, BigDecimal mntVen, String transactionType,
                                         BigDecimal marketPrice, Integer counterpartyId) {
        if (baseCurrencyId == null || quoteCurrencyId == null || counterpartyId == null) {
            throw new IllegalArgumentException("Currency IDs and counterparty ID cannot be null");
        }
        if (mntAcht == null || mntAcht.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid buy amount");
        }
        if (mntVen == null || mntVen.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid sell amount");
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
                .orElseThrow(() -> new TradingException(type + " currency not found with ID: " + currencyId));
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
        BigDecimal finalMntAcht = mntAcht;
        BigDecimal finalMntVen = mntVen;
        return new TradeCurrencyDetails(boughtCurrency, soldCurrency, finalMntAcht, finalMntVen);
    }

    private Transaction createTransaction(Currency boughtCurrency, Currency soldCurrency,
                                          BigDecimal finalMntAcht, BigDecimal finalMntVen,
                                          BigDecimal marketPrice, String transactionType,
                                          User user, Counterparty counterparty , LocalDate dateValue) {
        return new Transaction(boughtCurrency, soldCurrency, finalMntAcht, finalMntVen,
                marketPrice, transactionType.toUpperCase(), user, counterparty ,dateValue);
    }

    private Position getOrCreatePosition(Currency currency, User user) {
        return positionRepository.findByCurrency(currency)
                .orElseGet(() -> {
                    Position newPosition = new Position(currency, BigDecimal.ZERO, BigDecimal.ZERO, user);
                    return positionRepository.save(newPosition);
                });
    }

    private void validateFunds(Position soldPosition, BigDecimal mntVen, String transactionType) {

        if ("TND".equalsIgnoreCase(soldPosition.getCurrency().getIdentifier())) {
            return; // skip the check
        }
        if (soldPosition.getMntDev().compareTo(mntVen) < 0) {
            throw new InsufficientFundsException(String.format(
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
    }

    private void updatePositionList(Position... positions) {
        for (Position position : positions) {
            positionList.removeIf(p -> p.getPk().equals(position.getPk()));
            positionList.add(position);
        }
        logger.debug("Updated positionList: {}", positionList);
    }

    @Transactional
    public void setDailyNeeds(Integer currencyId, BigDecimal besoinDev, User user) {
        Currency currency = getCurrency(currencyId, "");
        Position position = getOrCreatePosition(currency, user);
        position.setBesoinDev(besoinDev != null ? besoinDev : BigDecimal.ZERO);
        positionRepository.save(position);
        updatePositionList(position);
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

    private PositionDTO toDTO(Position position) {
        PositionDTO dto = new PositionDTO();
        dto.setPk(position.getPk());
        dto.setIdentifier(position.getCurrency().getIdentifier());
        dto.setMntDev(position.getMntDev());
        dto.setBesoinDev(position.getBesoinDev());
        return dto;
    }

    private Position toEntity(PositionDTO dto) {
        Position position = new Position();
        position.setPk(dto.getPk());
        Currency currency = currencyRepository.findByIdentifier(dto.getIdentifier())
                .orElseThrow(() -> new IllegalArgumentException("Currency not found for identifier: " + dto.getIdentifier()));
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
                .orElseThrow(() -> new IllegalArgumentException("Currency not found for identifier: " + positionDTO.getIdentifier()));
        if (positionRepository.existsByCurrencyId(currency.getPk())) {
            throw new IllegalStateException("This currency already has an associated position");
        }
        Position position = toEntity(positionDTO);
        Position savedPosition = positionRepository.save(position);
        updatePositionList(savedPosition);
        broadcastPositions();
        return toDTO(savedPosition);
    }

    public List<PositionDTO> createPositions(List<PositionDTO> positionDTOs, User user) {
        List<Position> savedPositions = positionDTOs.stream().map(dto -> {
            Currency currency = currencyRepository.findByIdentifier(dto.getIdentifier())
                    .orElseThrow(() -> new IllegalArgumentException("Currency not found for identifier: " + dto.getIdentifier()));
            if (positionRepository.existsByCurrencyId(currency.getPk())) {
                throw new IllegalStateException("Currency with identifier " + dto.getIdentifier() + " already has an associated position");
            }
            Position position = toEntity(dto);
            position.setUser(user);
            return positionRepository.save(position);
        }).collect(Collectors.toList());

        savedPositions.forEach(position -> updatePositionList(position));
        broadcastPositions();
        return savedPositions.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Optional<PositionDTO> updatePosition(Integer id, PositionDTO positionDTO) {
        return positionRepository.findById(id).map(position -> {
            Currency newCurrency = currencyRepository.findByIdentifier(positionDTO.getIdentifier())
                    .orElseThrow(() -> new IllegalArgumentException("Currency not found for identifier: " + positionDTO.getIdentifier()));
            if (!position.getCurrency().getPk().equals(newCurrency.getPk()) &&
                    positionRepository.existsByCurrencyId(newCurrency.getPk())) {
                throw new IllegalStateException("The new currency already has an associated position");
            }
            position.setCurrency(newCurrency);
            position.setMntDev(positionDTO.getMntDev() != null ? positionDTO.getMntDev() : BigDecimal.ZERO);
            position.setBesoinDev(positionDTO.getBesoinDev() != null ? positionDTO.getBesoinDev() : BigDecimal.ZERO);
            Position updatedPosition = positionRepository.save(position);
            updatePositionList(updatedPosition);
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

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new TradingException("User not found with email: " + email));
    }

    public List<Position> getPositionsByUserId(Integer userId) {
        return positionRepository.findByUserId(userId);
    }
}