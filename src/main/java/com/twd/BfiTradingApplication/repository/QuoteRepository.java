package com.twd.BfiTradingApplication.repository;

import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Integer> {
    Quote findByCrossParity(CrossParity crossParity);

    @Query("SELECT q FROM Quote q JOIN FETCH q.crossParity cp JOIN FETCH cp.dailyStats ds")
    List<Quote> findAllWithDailyStats();

    @Query("SELECT q FROM Quote q WHERE q.crossParity.pk = :parityId ORDER BY q.quoteTime DESC LIMIT 1")
    Quote findLatestByParityId(@Param("parityId") Integer parityId);

    @Query("SELECT q FROM Quote q " +
            "WHERE q.crossParity.baseCurrency.pk = :baseCurrencyId " +
            "AND q.crossParity.quoteCurrency.pk = :quoteCurrencyId " +
            "ORDER BY q.quoteTime DESC LIMIT 1")
    Optional<Quote> findLatestByCrossParity(Integer baseCurrencyId, Integer quoteCurrencyId);



}
