package com.twd.BfiTradingApplication.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pending_order")
@Data
public class PendingOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "base_currency_id", nullable = false)
    private Currency baseCurrency;

    @ManyToOne
    @JoinColumn(name = "quote_currency_id", nullable = false)
    private Currency quoteCurrency;

    @Column(name = "amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;

    @Column(name = "target_price", precision = 19, scale = 4, nullable = false)
    private BigDecimal targetPrice;

    @Column(name = "order_type", nullable = false)
    private String orderType; // "BUY" or "SELL"

    @Column(name = "trigger_type", nullable = false)
    private String triggerType; // "STOP_LOSS" or "TAKE_PROFIT"

    @Column(name = "action_on_trigger", nullable = false)
    private String actionOnTrigger; // "EXECUTE" or "NOTIFY"

    @Column(name = "status", nullable = false)
    private String status; // "PENDING", "EXECUTED", "CANCELLED"

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public PendingOrder() {
        this.amount = BigDecimal.ZERO;
        this.targetPrice = BigDecimal.ZERO;
        this.orderType = "BUY";
        this.triggerType = "STOP_LOSS";
        this.actionOnTrigger = "EXECUTE";
        this.status = "PENDING";
    }

    public PendingOrder(Currency baseCurrency, Currency quoteCurrency, BigDecimal amount,
                        BigDecimal targetPrice, String orderType, String triggerType,
                        String actionOnTrigger, User user) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.targetPrice = targetPrice != null ? targetPrice : BigDecimal.ZERO;
        this.orderType = orderType != null ? orderType : "BUY";
        this.triggerType = triggerType != null ? triggerType : "STOP_LOSS";
        this.actionOnTrigger = actionOnTrigger != null ? actionOnTrigger : "EXECUTE";
        this.status = "PENDING";
        this.user = user;
    }
}