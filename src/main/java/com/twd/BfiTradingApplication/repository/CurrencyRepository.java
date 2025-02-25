package com.twd.BfiTradingApplication.repository;

import com.twd.BfiTradingApplication.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
    Optional<Currency> findByIdentifier(String identifier);

}