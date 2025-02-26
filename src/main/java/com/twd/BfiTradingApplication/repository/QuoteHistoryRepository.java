package com.twd.BfiTradingApplication.repository;

import com.twd.BfiTradingApplication.entity.QuoteHistory;
import com.twd.BfiTradingApplication.entity.QuoteHistoryId;
import com.twd.BfiTradingApplication.entity.CrossParity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuoteHistoryRepository extends JpaRepository<QuoteHistory, QuoteHistoryId> {

    @Query("SELECT qh FROM QuoteHistory qh WHERE qh.crossParity = :crossParity AND qh.pk.quoteTime >= :quoteTime")
    List<QuoteHistory> findByCrossParityAndPkQuoteTimeAfter(
            @Param("crossParity") CrossParity crossParity,
            @Param("quoteTime") LocalDateTime quoteTime
    );

    // Utiliser "id.quoteTime" pour référencer la clé composite correctement
    @Query("SELECT qh FROM QuoteHistory qh WHERE qh.crossParity = :crossParity AND qh.pk.quoteTime BETWEEN :startDate AND :endDate")
    List<QuoteHistory> findByCrossParityAndPkQuoteTimeBetween(
            @Param("crossParity") CrossParity crossParity,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );



}
