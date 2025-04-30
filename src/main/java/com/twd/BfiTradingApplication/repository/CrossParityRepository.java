package com.twd.BfiTradingApplication.repository;

import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CrossParityRepository extends JpaRepository<CrossParity, Integer> {
    boolean existsByIdentifier(String identifier);
    CrossParity findByIdentifier(String identifier);

    Optional<CrossParity> findByBaseCurrencyAndQuoteCurrency(Currency baseCurrency, Currency quoteCurrency);

//    Optional<CrossParity> findByIdentifier(String identifier);


}






