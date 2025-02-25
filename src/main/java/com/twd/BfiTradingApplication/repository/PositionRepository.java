package com.twd.BfiTradingApplication.repository;

import com.twd.BfiTradingApplication.entity.Position;
import com.twd.BfiTradingApplication.entity.PositionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Integer> {
    List<Position> findByCrossParityPk(Integer crossParityPk);
    List<Position> findByStatus(PositionStatus status);
    List<Position> findByCrossParityPkAndStatus(Integer crossParityPk, PositionStatus status);
}