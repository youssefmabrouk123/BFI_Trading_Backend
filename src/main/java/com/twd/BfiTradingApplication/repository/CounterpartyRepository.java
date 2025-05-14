package com.twd.BfiTradingApplication.repository;

import com.twd.BfiTradingApplication.entity.Counterparty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CounterpartyRepository extends JpaRepository<Counterparty, Integer> {
}
