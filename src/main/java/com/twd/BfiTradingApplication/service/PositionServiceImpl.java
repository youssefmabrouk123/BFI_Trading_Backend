//package com.twd.BfiTradingApplication.service   ;
//
//import com.twd.BfiTradingApplication.entity.*;
//import com.twd.BfiTradingApplication.repository.PositionRepository;
//import com.twd.BfiTradingApplication.repository.QuoteRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//@Service
//public class PositionServiceImpl {
//
//    @Autowired
//    private PositionRepository positionRepository;
//
//    @Autowired
//    private QuoteRepository quoteRepository;
//
//    private final Random random = new Random();
//
//    // Générer des positions initiales
//    public void initializePositions() {
//        List<Quote> quotes = quoteRepository.findAll();
//        List<Position> positions = new ArrayList<>();
//
//        for (Quote quote : quotes) {
//            if (random.nextDouble() < 0.7) { // 70% de chance de créer une position pour chaque paire
//                Position position = createPosition(quote);
//                positions.add(position);
//            }
//        }
//        positionRepository.saveAll(positions);
//    }
//
//    private Position createPosition(Quote quote) {
//        Position position = new Position();
//        position.setCrossParity(quote.getCrossParity());
//        position.setType(random.nextBoolean() ? PositionType.BUY : PositionType.SELL);
//
//        // Définir le volume
//        double[] volumes = {10000.0, 25000.0, 50000.0, 100000.0};
//        position.setVolume(volumes[random.nextInt(volumes.length)]);
//
//        // Prix d'ouverture basé sur le type de position
//        if (position.getType() == PositionType.BUY) {
//            position.setOpenPrice(quote.getAskPrice().doubleValue());
//            position.setStopLoss(position.getOpenPrice() - (position.getOpenPrice() * 0.01)); // -1%
//            position.setTakeProfit(position.getOpenPrice() + (position.getOpenPrice() * 0.02)); // +2%
//        } else {
//            position.setOpenPrice(quote.getBidPrice().doubleValue());
//            position.setStopLoss(position.getOpenPrice() + (position.getOpenPrice() * 0.01)); // +1%
//            position.setTakeProfit(position.getOpenPrice() - (position.getOpenPrice() * 0.02)); // -2%
//        }
//
//        position.setOpenDate(LocalDateTime.now().minusHours(random.nextInt(48)));
//        position.setStatus(PositionStatus.OPEN);
//        position.setCurrentPrice(position.getOpenPrice());
//        updateProfitLoss(position, quote);
//
//        return position;
//    }
//
//    // Mise à jour des positions toutes les 3 secondes (synchronisé avec QuoteSimulationService)
//    @Scheduled(fixedRate = 3000)
//    public void updatePositions() {
//        List<Position> openPositions = positionRepository.findByStatus(PositionStatus.OPEN);
//
//        for (Position position : openPositions) {
//            Quote latestQuote = quoteRepository.findByCrossParity(position.getCrossParity());
//            if (latestQuote != null) {
//                updatePosition(position, latestQuote);
//            }
//        }
//    }
//
//    private void updatePosition(Position position, Quote quote) {
//        // Mettre à jour le prix actuel selon le type de position
//        if (position.getType() == PositionType.BUY) {
//            position.setCurrentPrice(quote.getBidPrice().doubleValue()); // Prix de vente pour les positions longues
//        } else {
//            position.setCurrentPrice(quote.getAskPrice().doubleValue()); // Prix d'achat pour les positions courtes
//        }
//
//        // Vérifier Stop Loss et Take Profit
//        boolean shouldClose = false;
//        if (position.getType() == PositionType.BUY) {
//            if (position.getCurrentPrice() <= position.getStopLoss() ||
//                    position.getCurrentPrice() >= position.getTakeProfit()) {
//                shouldClose = true;
//            }
//        } else {
//            if (position.getCurrentPrice() >= position.getStopLoss() ||
//                    position.getCurrentPrice() <= position.getTakeProfit()) {
//                shouldClose = true;
//            }
//        }
//
//        // Mettre à jour P/L
//        updateProfitLoss(position, quote);
//
//        // Fermer la position si nécessaire
//        if (shouldClose) {
//            position.setStatus(PositionStatus.CLOSED);
//            position.setCloseDate(LocalDateTime.now());
//        }
//
//        positionRepository.save(position);
//    }
//
//    private void updateProfitLoss(Position position, Quote quote) {
//        double priceDiff;
//        if (position.getType() == PositionType.BUY) {
//            priceDiff = position.getCurrentPrice() - position.getOpenPrice();
//        } else {
//            priceDiff = position.getOpenPrice() - position.getCurrentPrice();
//        }
//
//        position.setProfitLoss(priceDiff * position.getVolume());
//    }
//
//    // API endpoints pour récupérer les positions
//    public List<Position> getAllPositions() {
//        return positionRepository.findAll();
//    }
//
//    public List<Position> getOpenPositions() {
//        return positionRepository.findByStatus(PositionStatus.OPEN);
//    }
//}