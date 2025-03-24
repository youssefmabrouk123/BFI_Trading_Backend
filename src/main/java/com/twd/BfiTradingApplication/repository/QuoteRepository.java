package com.twd.BfiTradingApplication.repository;

import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Integer> {
    Quote findByCrossParity(CrossParity crossParity);

    @Query("SELECT q FROM Quote q JOIN FETCH q.crossParity cp JOIN FETCH cp.dailyStats ds")
    List<Quote> findAllWithDailyStats();

    @Query("SELECT q FROM Quote q WHERE q.crossParity.pk = :parityId ORDER BY q.quoteTime DESC LIMIT 1")
    Quote findLatestByParityId(@Param("parityId") Integer parityId);
}
