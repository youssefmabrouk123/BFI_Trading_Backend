package com.twd.BfiTradingApplication.repository;

import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.entity.DailyStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyStatsRepository extends JpaRepository<DailyStats, Integer> {
    List<DailyStats> findByCrossParityIdentifierAndDateBetween(
            String identifier, LocalDate startDate, LocalDate endDate);

    boolean existsByCrossParityIdentifierAndDate(String identifier, LocalDate date);

    Optional<DailyStats> findByCrossParityAndDate(CrossParity crossParity, LocalDate date);



//    aaaaaaa

    @Query("SELECT ds FROM DailyStats ds WHERE ds.crossParity.pk = :parityId " +
            "AND ds.date BETWEEN :startDate AND :endDate " +
            "ORDER BY ds.date")
    List<DailyStats> findByParityIdAndDateRange(
            @Param("parityId") Integer parityId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

//    aaaaaaa




}