package com.twd.BfiTradingApplication.repository;
import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.entity.Position;
import com.twd.BfiTradingApplication.entity.PositionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Integer> {

    Optional<Position> findByCrossParityAndStatus(CrossParity crossParity, PositionStatus status);


    // Find all open positions for a specific CrossParity
//    List<Position> findByCrossParityAndStatus(CrossParity crossParity, PositionStatus status);

    // Find all open positions
    List<Position> findByStatus(PositionStatus status);

    // Find all positions for a cross parity with a given status
    List<Position> findAllByCrossParityAndStatus(CrossParity crossParity, PositionStatus status);


//aaaa

    @Query("SELECT p FROM Position p WHERE p.crossParity.pk = :parityId ORDER BY p.openTime DESC")
    List<Position> findByParityId(@Param("parityId") Integer parityId);

    @Query("SELECT p FROM Position p WHERE p.crossParity.pk = :parityId AND p.status = :status ORDER BY p.openTime DESC")
    List<Position> findByParityIdAndStatus(
            @Param("parityId") Integer parityId,
            @Param("status") PositionStatus status);

//aaaa


}