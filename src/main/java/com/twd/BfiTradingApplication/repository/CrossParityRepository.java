package com.twd.BfiTradingApplication.repository;

import com.twd.BfiTradingApplication.entity.CrossParity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrossParityRepository extends JpaRepository<CrossParity, Integer> {
    boolean existsByIdentifier(String identifier);

}
