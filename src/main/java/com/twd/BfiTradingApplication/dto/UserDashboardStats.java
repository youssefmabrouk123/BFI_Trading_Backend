//package com.twd.BfiTradingApplication.dto;
//
//import com.twd.BfiTradingApplication.entity.UserAction;
//import lombok.Data;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//
//@Data
//public class UserDashboardStats {
//    private long transactionCount; // Nombre total de transactions
//    private BigDecimal totalVolume; // Volume total des transactions (en devise de référence, ex: USD)
//    private BigDecimal totalProfitLoss; // Profit/Perte total
//    private LocalDateTime lastAction; // Date de la dernière action
//    private List<UserAction> recentTransactions; // Liste des transactions récentes
//    private Map<String, BigDecimal> volumeByCurrency; // Volume par devise
//    private Map<String, Integer> transactionCountByType; // Nombre de transactions par type (BUY/SELL)
//    private List<ProfitLossByDay> profitLossByDay; // Profit/Perte par jour pour graphique
//    private BigDecimal currentPortfolioValue; // Valeur actuelle du portefeuille
//}


package com.twd.BfiTradingApplication.dto;

import com.twd.BfiTradingApplication.entity.UserAction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class UserDashboardStats {
    // Métriques existantes
    private long transactionCount;
    private BigDecimal totalVolume;
    private BigDecimal totalProfitLoss;
    private LocalDateTime lastAction;
    private List<UserAction> recentTransactions;
    private Map<String, BigDecimal> volumeByCurrency;
    private Map<String, Integer> transactionCountByType;
    private List<ProfitLossByDay> profitLossByDay;
    private BigDecimal currentPortfolioValue;

    // Nouvelles métriques pour le dashboard des cambistes
    private List<PerformanceMetric> performanceMetrics; // Métriques de performance
    private Map<String, List<BigDecimal>> exchangeRateTrends; // Tendances des taux de change
    private Map<Integer, Integer> transactionsByHour; // Transactions par heure
    private BigDecimal successRate; // Taux de succès (transactions profitables)
}