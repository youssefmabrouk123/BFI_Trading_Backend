package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.entity.DailyStats;
import com.twd.BfiTradingApplication.entity.Quote;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface QuoteSimulationService {
     void simulateQuotes();

     // Méthode de génération de prix aléatoire
     BigDecimal generateRandomPrice();

     // Méthodes de calcul des variations
     BigDecimal calculateNetVar(CrossParity crossParity, BigDecimal newBidPrice);
     BigDecimal calculatePercentageVar(CrossParity crossParity, BigDecimal newBidPrice);
}