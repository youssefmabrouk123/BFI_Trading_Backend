//package com.twd.BfiTradingApplication.service;
//
//import com.twd.BfiTradingApplication.dto.ProfitLossByDay;
//import com.twd.BfiTradingApplication.dto.UserDashboardStats;
//import com.twd.BfiTradingApplication.entity.*;
//import com.twd.BfiTradingApplication.repository.PositionRepository;
//import com.twd.BfiTradingApplication.repository.TransactionRepository;
//import com.twd.BfiTradingApplication.repository.UserActionRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class UserDashboardService {
//
//    @Autowired
//    private UserActionRepository userActionRepository;
//
//    @Autowired
//    private TransactionRepository transactionRepository;
//
//    @Autowired
//    private PositionRepository positionRepository;
//
//    public UserDashboardStats getUserStats(Integer userId) {
//        UserDashboardStats stats = new UserDashboardStats();
//
//        // Récupérer les données de base
//        List<UserAction> actions = userActionRepository.findByUserId(userId);
//        List<Transaction> transactions = transactionRepository.findByUserId(userId);
//        List<Position> positions = positionRepository.findByUserId(userId);
//
//        // 1. Nombre total de transactions
//        stats.setTransactionCount(transactions.size());
//
//        // 2. Volume total des transactions (somme des montants achetés)
//        BigDecimal totalVolume = transactions.stream()
//                .map(Transaction::getMntAcht)
//                .filter(Objects::nonNull)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        stats.setTotalVolume(totalVolume.setScale(4, RoundingMode.HALF_UP));
//
//        // 3. Profit/Perte total (simplifié, basé sur les positions actuelles et les transactions)
//        BigDecimal totalProfitLoss = calculateTotalProfitLoss(transactions, positions);
//        stats.setTotalProfitLoss(totalProfitLoss);
//
//        // 4. Dernière action
//        stats.setLastAction(actions.isEmpty() ? null : actions.get(0).getActionTime());
//
//        // 5. Transactions récentes (limitées à 5)
//        stats.setRecentTransactions(transactions.stream()
//                .sorted(Comparator.comparing(Transaction::getId, Comparator.reverseOrder()))
//                .limit(5)
//                .map(this::toUserAction) // Conversion en UserAction pour cohérence
//                .collect(Collectors.toList()));
//
//        // 6. Volume par devise
//        Map<String, BigDecimal> volumeByCurrency = transactions.stream()
//                .collect(Collectors.groupingBy(
//                        t -> t.getDevAchn().getIdentifier(),
//                        Collectors.reducing(BigDecimal.ZERO, Transaction::getMntAcht, BigDecimal::add)
//                ));
//        stats.setVolumeByCurrency(volumeByCurrency);
//
//        // 7. Nombre de transactions par type (BUY/SELL)
//        Map<String, Integer> transactionCountByType = transactions.stream()
//                .collect(Collectors.groupingBy(
//                        Transaction::getTransactionType,
//                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
//                ));
//        stats.setTransactionCountByType(transactionCountByType);
//
//        // 8. Profit/Perte par jour (pour graphique)
//        Map<LocalDate, BigDecimal> profitLossByDayMap = calculateProfitLossByDay(transactions);
//        List<ProfitLossByDay> profitLossByDay = profitLossByDayMap.entrySet().stream()
//                .map(entry -> {
//                    ProfitLossByDay pl = new ProfitLossByDay();
//                    pl.setDate(entry.getKey().atStartOfDay());
//                    pl.setProfitLoss(entry.getValue());
//                    return pl;
//                })
//                .sorted(Comparator.comparing(ProfitLossByDay::getDate))
//                .collect(Collectors.toList());
//        stats.setProfitLossByDay(profitLossByDay);
//
//        // 9. Valeur actuelle du portefeuille (simplifié, basé sur les positions)
//        BigDecimal currentPortfolioValue = calculatePortfolioValue(positions);
//        stats.setCurrentPortfolioValue(currentPortfolioValue);
//
//        return stats;
//    }
//
//    private BigDecimal calculateTotalProfitLoss(List<Transaction> transactions, List<Position> positions) {
//        // Calcul simplifié : somme des profits/pertes basés sur les transactions
//        // Pour un calcul précis, vous auriez besoin des prix d'entrée et de sortie réels
//        BigDecimal profitLoss = BigDecimal.ZERO;
//        for (Transaction t : transactions) {
//            if ("BUY".equalsIgnoreCase(t.getTransactionType())) {
//                // Pour un achat, on ne calcule pas encore le profit (position ouverte)
//                continue;
//            } else {
//                // Pour une vente, on suppose un profit/perte basé sur le prix d'achat initial
//                // Ici, simplification : différence entre mntVen et mntAcht ajustée par marketPrice
//                BigDecimal cost = t.getMntAcht().multiply(t.getPrice());
//                profitLoss = profitLoss.add(t.getMntVen().subtract(cost));
//            }
//        }
//        return profitLoss.setScale(4, RoundingMode.HALF_UP);
//    }
//
//    private Map<LocalDate, BigDecimal> calculateProfitLossByDay(List<Transaction> transactions) {
//        return transactions.stream()
//                .filter(t -> "SELL".equalsIgnoreCase(t.getTransactionType())) // On calcule le profit sur les ventes
//                .collect(Collectors.groupingBy(
//                        t -> t.getId() != null ? LocalDate.now() : LocalDate.now(), // Simplification : date fictive
//                        Collectors.reducing(BigDecimal.ZERO, t -> {
//                            BigDecimal cost = t.getMntAcht().multiply(t.getPrice());
//                            return t.getMntVen().subtract(cost);
//                        }, BigDecimal::add)
//                ));
//    }
//
//    private BigDecimal calculatePortfolioValue(List<Position> positions) {
//        // Simplification : somme des montants en devise (mntDev) sans conversion
//        return positions.stream()
//                .map(Position::getMntDev)
//                .filter(Objects::nonNull)
//                .reduce(BigDecimal.ZERO, BigDecimal::add)
//                .setScale(4, RoundingMode.HALF_UP);
//    }
//
//    private UserAction toUserAction(Transaction transaction) {
//        UserAction action = new UserAction();
//        action.setId(transaction.getId());
//        action.setUser(transaction.getUser());
//        action.setActionType("TRANSACTION");
//        action.setDetails(String.format("%s %s of %s for %s %s",
//                transaction.getTransactionType(),
//                transaction.getMntAcht(),
//                transaction.getDevAchn().getIdentifier(),
//                transaction.getMntVen(),
//                transaction.getDevVen().getIdentifier()));
//        action.setAmount(transaction.getMntAcht());
//        action.setCurrency(transaction.getDevAchn());
//        action.setActionTime(LocalDateTime.now()); // À ajuster si vous avez une date dans Transaction
//        return action;
//    }
//}



package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.dto.PerformanceMetric;
import com.twd.BfiTradingApplication.dto.ProfitLossByDay;
import com.twd.BfiTradingApplication.dto.UserDashboardStats;
import com.twd.BfiTradingApplication.entity.*;
import com.twd.BfiTradingApplication.repository.PositionRepository;
import com.twd.BfiTradingApplication.repository.TransactionRepository;
import com.twd.BfiTradingApplication.repository.UserActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserDashboardService {

    @Autowired
    private UserActionRepository userActionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PositionRepository positionRepository;

    public UserDashboardStats getUserStats(Integer userId) {
        UserDashboardStats stats = new UserDashboardStats();

        // Récupérer les données de base
        List<UserAction> actions = userActionRepository.findByUserId(userId);
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        List<Position> positions = positionRepository.findByUserId(userId);

        List<Position> positionAll = positionRepository.findAll();


        BigDecimal tndPosition = positionAll.stream()
                .filter(pos -> "TND".equalsIgnoreCase(pos.getCurrency().getIdentifier()))
                .map(Position::getMntDev)
                .findFirst()
                .orElse(BigDecimal.ZERO);
        stats.setPosition(tndPosition.setScale(3, RoundingMode.HALF_UP));

        // 1. Nombre total de transactions
        stats.setTransactionCount(transactions.size());

        // 2. Volume total des transactions (somme des montants achetés)
        BigDecimal totalVolume = transactions.stream()
                .map(Transaction::getMntAcht)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalVolume(totalVolume.setScale(4, RoundingMode.HALF_UP));

        // 3. Profit/Perte total (amélioré)
        BigDecimal totalProfitLoss = calculateTotalProfitLoss(transactions, positions);
        stats.setTotalProfitLoss(totalProfitLoss);

        // 4. Dernière action
        Optional<UserAction> latestAction = actions.stream()
                .max(Comparator.comparing(UserAction::getActionTime));
        stats.setLastAction(latestAction.orElse(null) != null ? latestAction.get().getActionTime() : null);

        // 5. Transactions récentes (limitées à 5)
        stats.setRecentTransactions(transactions.stream()
                .sorted(Comparator.comparing(Transaction::getTransactionTime, Comparator.reverseOrder()))
                .limit(5)
                .map(this::toUserAction)
                .collect(Collectors.toList()));

        // 6. Volume par devise (pour graphique)
//        Map<String, BigDecimal> volumeByCurrency = transactions.stream()
//                .collect(Collectors.groupingBy(
//                        t -> t.getDevAchn().getIdentifier(),
//                        Collectors.reducing(BigDecimal.ZERO, Transaction::getMntAcht, BigDecimal::add)
//                ));
//        stats.setVolumeByCurrency(volumeByCurrency);

        Map<String, BigDecimal> volumeByCurrency = new HashMap<>();

        for (Transaction t : transactions) {
            // Ajouter le montant d'achat dans sa propre devise
            String devAcht = t.getDevAchn().getIdentifier();
            BigDecimal mntAcht = t.getMntAcht();
            volumeByCurrency.merge(devAcht, mntAcht, BigDecimal::add);

            // Ajouter le montant de vente dans sa propre devise
            String devVente = t.getDevVen().getIdentifier();
            BigDecimal mntVente = t.getMntVen();
            volumeByCurrency.merge(devVente, mntVente, BigDecimal::add);
        }

        stats.setVolumeByCurrency(volumeByCurrency);



        // 7. Nombre de transactions par type (BUY/SELL)
        Map<String, Integer> transactionCountByType = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getTransactionType,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
        stats.setTransactionCountByType(transactionCountByType);

        // 8. Profit/Perte par jour (pour graphique)
        List<ProfitLossByDay> profitLossByDay = calculateDailyProfitLoss(transactions);
        stats.setProfitLossByDay(profitLossByDay);

        // 9. Valeur actuelle du portefeuille
        BigDecimal currentPortfolioValue = calculatePortfolioValue(positions);
        stats.setCurrentPortfolioValue(currentPortfolioValue);

        // 10. NOUVEAU: Métriques de performance du cambiste
        stats.setPerformanceMetrics(calculatePerformanceMetrics(transactions));

        // 11. NOUVEAU: Tendance des taux de change pour les devises principales
        stats.setExchangeRateTrends(calculateExchangeRateTrends(transactions));

        // 12. NOUVEAU: Transactions par heure (pour identifier les heures de pointe)
        stats.setTransactionsByHour(calculateTransactionsByHour(transactions));

        // 13. NOUVEAU: Taux de succès des transactions (profitable vs non-profitable)
        stats.setSuccessRate(calculateSuccessRate(transactions));


        return stats;
    }

    private BigDecimal calculateTotalProfitLoss(List<Transaction> transactions, List<Position> positions) {
        // Calcul amélioré: nous prenons en compte à la fois les transactions réalisées
        // et les positions ouvertes valorisées au prix du marché
        BigDecimal realizedProfitLoss = transactions.stream()
                .filter(t -> "SELL".equalsIgnoreCase(t.getTransactionType()))
                .map(t -> {
                    // Pour une vente, nous calculons le profit comme: montant vendu - coût d'achat
                    BigDecimal cost = t.getMntAcht().multiply(t.getPrice());
                    return t.getMntVen().subtract(cost);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Note: Pour un dashboard complet, il faudrait ajouter le calcul des profits/pertes
        // non réalisés sur les positions ouvertes (nécessiterait les prix actuels du marché)

        return realizedProfitLoss.setScale(4, RoundingMode.HALF_UP);
    }

    private List<ProfitLossByDay> calculateDailyProfitLoss(List<Transaction> transactions) {
        // Regrouper les transactions par jour et calculer le profit/perte
        Map<LocalDate, BigDecimal> profitByDay = transactions.stream()
                .filter(t -> t.getTransactionTime() != null && "SELL".equalsIgnoreCase(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionTime().toLocalDate(),
                        Collectors.reducing(BigDecimal.ZERO, t -> {
                            BigDecimal cost = t.getMntAcht().multiply(t.getPrice());
                            return t.getMntVen().subtract(cost);
                        }, BigDecimal::add)
                ));

        // Créer une liste continue de dates (pour combler les jours sans transactions)
        if (!profitByDay.isEmpty()) {
            LocalDate minDate = profitByDay.keySet().stream().min(LocalDate::compareTo).orElse(LocalDate.now().minusDays(30));
            LocalDate maxDate = profitByDay.keySet().stream().max(LocalDate::compareTo).orElse(LocalDate.now());

            List<ProfitLossByDay> result = new ArrayList<>();
            for (LocalDate date = minDate; !date.isAfter(maxDate); date = date.plusDays(1)) {
                ProfitLossByDay pl = new ProfitLossByDay();
                pl.setDate(date.atStartOfDay());
                pl.setProfitLoss(profitByDay.getOrDefault(date, BigDecimal.ZERO));
                result.add(pl);
            }
            return result;
        }

        return new ArrayList<>();
    }

    private BigDecimal calculatePortfolioValue(List<Position> positions) {
        // Calculer la valeur du portefeuille en sommant les positions
        return positions.stream()
                .map(Position::getMntDev)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(4, RoundingMode.HALF_UP);
    }

    private List<PerformanceMetric> calculatePerformanceMetrics(List<Transaction> transactions) {
        // Calculer les métriques de performance pour le cambiste
        List<PerformanceMetric> metrics = new ArrayList<>();

        // 1. Rendement moyen par transaction
        if (!transactions.isEmpty()) {
            BigDecimal totalProfit = transactions.stream()
                    .filter(t -> "SELL".equalsIgnoreCase(t.getTransactionType()))
                    .map(t -> {
                        BigDecimal cost = t.getMntAcht().multiply(t.getPrice());
                        return t.getMntVen().subtract(cost);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long profitableTransactions = transactions.stream()
                    .filter(t -> "SELL".equalsIgnoreCase(t.getTransactionType()))
                    .filter(t -> {
                        BigDecimal cost = t.getMntAcht().multiply(t.getPrice());
                        return t.getMntVen().subtract(cost).compareTo(BigDecimal.ZERO) > 0;
                    })
                    .count();

            metrics.add(new PerformanceMetric("Rendement moyen",
                    totalProfit.divide(new BigDecimal(Math.max(1, transactions.size())), 4, RoundingMode.HALF_UP)));

            metrics.add(new PerformanceMetric("Taux de transactions profitables",
                    new BigDecimal(profitableTransactions)
                            .divide(new BigDecimal(Math.max(1, transactions.size())), 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal(100))));
        }

        // 2. Volume quotidien moyen
        Map<LocalDate, BigDecimal> volumeByDay = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionTime().toLocalDate(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getMntAcht, BigDecimal::add)
                ));

        if (!volumeByDay.isEmpty()) {
            BigDecimal totalVolume = volumeByDay.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avgDailyVolume = totalVolume.divide(new BigDecimal(volumeByDay.size()), 4, RoundingMode.HALF_UP);
            metrics.add(new PerformanceMetric("Volume quotidien moyen", avgDailyVolume));
        }

        return metrics;
    }

    private Map<String, List<BigDecimal>> calculateExchangeRateTrends(List<Transaction> transactions) {
        // Calculer les tendances des taux de change pour les principales paires de devises
        Map<String, List<BigDecimal>> trends = new HashMap<>();

        // Regrouper par paire de devises
        Map<String, List<Transaction>> transactionsByPair = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getDevAchn().getIdentifier() + "/" + t.getDevVen().getIdentifier()
                ));

        // Pour chaque paire, extraire les taux chronologiquement
        transactionsByPair.forEach((pair, txList) -> {
            List<BigDecimal> rates = txList.stream()
                    .sorted(Comparator.comparing(Transaction::getTransactionTime))
                    .map(Transaction::getPrice)
                    .collect(Collectors.toList());

            if (!rates.isEmpty()) {
                trends.put(pair, rates);
            }
        });

        return trends;
    }

    private Map<Integer, Integer> calculateTransactionsByHour(List<Transaction> transactions) {
        // Calculer le nombre de transactions par heure de la journée
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionTime().getHour(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }

    private BigDecimal calculateSuccessRate(List<Transaction> transactions) {
        // Calculer le taux de succès des transactions (profitable vs non-profitable)
        long totalSellTransactions = transactions.stream()
                .filter(t -> "SELL".equalsIgnoreCase(t.getTransactionType()))
                .count();

        if (totalSellTransactions == 0) {
            return BigDecimal.ZERO;
        }

        long profitableTransactions = transactions.stream()
                .filter(t -> "SELL".equalsIgnoreCase(t.getTransactionType()))
                .filter(t -> {
                    BigDecimal cost = t.getMntAcht().multiply(t.getPrice());
                    return t.getMntVen().subtract(cost).compareTo(BigDecimal.ZERO) > 0;
                })
                .count();

        return new BigDecimal(profitableTransactions)
                .divide(new BigDecimal(totalSellTransactions), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)); // En pourcentage
    }

    private UserAction toUserAction(Transaction transaction) {
        UserAction action = new UserAction();
        action.setId(transaction.getId());
        action.setUser(transaction.getUser());
        action.setActionType("TRANSACTION_" + transaction.getTransactionType());
        action.setDetails(String.format("%s %s %s pour %s %s à %s",
                transaction.getTransactionType(),
                transaction.getMntAcht(),
                transaction.getDevAchn().getIdentifier(),
                transaction.getMntVen(),
                transaction.getDevVen().getIdentifier(),
                transaction.getPrice()));
        action.setAmount(transaction.getMntAcht());
        action.setCurrency(transaction.getDevAchn());
        action.setActionTime(transaction.getTransactionTime());
        return action;
    }
}