package com.twd.BfiTradingApplication.repository;

import com.twd.BfiTradingApplication.entity.Currency;
import com.twd.BfiTradingApplication.entity.CurrencyDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CurrencyDetailRepository extends JpaRepository<CurrencyDetail, Long> {
    CurrencyDetail findByCurrency(Currency currency); // Une seule instance par Currency
}