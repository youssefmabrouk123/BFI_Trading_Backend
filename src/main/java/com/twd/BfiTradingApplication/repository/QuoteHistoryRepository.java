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


    QuoteHistory findTopByCrossParityOrderByPkQuoteTimeDesc(CrossParity crossParity);

    List<QuoteHistory> findByCrossParity_PkAndPk_QuoteTimeBetweenOrderByPk_QuoteTimeAsc(
            Integer crossParityPk, LocalDateTime startTime, LocalDateTime endTime);

//    @Query("SELECT qh FROM QuoteHistory qh WHERE qh.crossParity.pk = ?1 AND qh.pk.quoteTime BETWEEN ?2 AND ?3 ORDER BY qh.pk.quoteTime ASC")
//    List<QuoteHistory> findByCrossParityAndQuoteTimeBetweenOrderByQuoteTimeAsc(CrossParity crossParity, LocalDateTime startDateTime, LocalDateTime endDateTime);
@Query("SELECT qh FROM QuoteHistory qh WHERE qh.crossParity = ?1 AND qh.pk.quoteTime BETWEEN ?2 AND ?3 ORDER BY qh.pk.quoteTime ASC")
List<QuoteHistory> findByCrossParityAndQuoteTimeBetweenOrderByQuoteTimeAsc(CrossParity crossParity, LocalDateTime startDateTime, LocalDateTime endDateTime);



    List<QuoteHistory> findByCrossParityAndPk_QuoteTimeBetween(CrossParity crossParity, LocalDateTime start, LocalDateTime end);
    @Query("SELECT q FROM QuoteHistory q WHERE q.crossParity.pk = ?1 AND q.pk.quoteTime BETWEEN ?2 AND ?3 ORDER BY q.pk.quoteTime")
    List<QuoteHistory> findByCrossParityPkAndQuoteTimeBetween(Integer crossParityId, LocalDateTime startTime, LocalDateTime endTime);

}
