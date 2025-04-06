package com.twd.BfiTradingApplication.repository;

import com.twd.BfiTradingApplication.entity.PendingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PendingOrderRepository extends JpaRepository<PendingOrder, Integer> {


    List<PendingOrder> findByStatus(String status);



    List<PendingOrder> findByUserIdAndStatus(Integer userId, String status);

    @Query("SELECT p FROM PendingOrder p WHERE p.status = 'PENDING' AND " +
            "p.baseCurrency.pk = :baseCurrencyId AND p.quoteCurrency.pk = :quoteCurrencyId")
    List<PendingOrder> findPendingOrdersByCurrencyPair(Integer baseCurrencyId, Integer quoteCurrencyId);

    @Query("SELECT p FROM PendingOrder p WHERE p.status = 'PENDING' AND " +
            "p.baseCurrency.pk = :baseCurrencyId AND p.quoteCurrency.pk = :quoteCurrencyId AND " +
            "p.orderType = 'BUY' AND p.triggerType = 'STOP_LOSS' AND p.targetPrice >= :currentPrice")
    List<PendingOrder> findBuyStopLossToTrigger(Integer baseCurrencyId, Integer quoteCurrencyId, BigDecimal currentPrice);

    @Query("SELECT p FROM PendingOrder p WHERE p.status = 'PENDING' AND " +
            "p.baseCurrency.pk = :baseCurrencyId AND p.quoteCurrency.pk = :quoteCurrencyId AND " +
            "p.orderType = 'BUY' AND p.triggerType = 'TAKE_PROFIT' AND p.targetPrice <= :currentPrice")
    List<PendingOrder> findBuyTakeProfitToTrigger(Integer baseCurrencyId, Integer quoteCurrencyId, BigDecimal currentPrice);

    @Query("SELECT p FROM PendingOrder p WHERE p.status = 'PENDING' AND " +
            "p.baseCurrency.pk = :baseCurrencyId AND p.quoteCurrency.pk = :quoteCurrencyId AND " +
            "p.orderType = 'SELL' AND p.triggerType = 'STOP_LOSS' AND p.targetPrice <= :currentPrice")
    List<PendingOrder> findSellStopLossToTrigger(Integer baseCurrencyId, Integer quoteCurrencyId, BigDecimal currentPrice);

    @Query("SELECT p FROM PendingOrder p WHERE p.status = 'PENDING' AND " +
            "p.baseCurrency.pk = :baseCurrencyId AND p.quoteCurrency.pk = :quoteCurrencyId AND " +
            "p.orderType = 'SELL' AND p.triggerType = 'TAKE_PROFIT' AND p.targetPrice >= :currentPrice")
    List<PendingOrder> findSellTakeProfitToTrigger(Integer baseCurrencyId, Integer quoteCurrencyId, BigDecimal currentPrice);
}