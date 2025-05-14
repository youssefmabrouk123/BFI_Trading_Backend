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




    List<DailyStats> findByCrossParityAndDateBetweenOrderByDateAsc(CrossParity crossParity, LocalDate startDate, LocalDate endDate);


    List<DailyStats> findByCrossParityPkAndDateBetween(Integer crossParityId, LocalDate startDate, LocalDate endDate);


    List<DailyStats> findByCrossParityPk(Integer crossParityId);
    List<DailyStats> findByCrossParityPkAndDate(Integer crossParityId, LocalDate date);

}