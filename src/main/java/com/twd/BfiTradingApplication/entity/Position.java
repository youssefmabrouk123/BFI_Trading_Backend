package com.twd.BfiTradingApplication.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
@Entity
@Table(name = "position")
@Data
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;

    @OneToOne
    @JoinColumn(name = "currency_id", nullable = false , unique = true)
    private Currency currency;

    @Column(name = "mnt_dev", precision = 19, scale = 4, nullable = false)
    private BigDecimal mntDev;

    @Column(name = "besoin_dev", precision = 19, scale = 4, nullable = false)
    private BigDecimal besoinDev;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Position() {
        this.mntDev = BigDecimal.ZERO;
        this.besoinDev = BigDecimal.ZERO;
    }

    public Position(Currency currency, BigDecimal mntDev, BigDecimal besoinDev, User user) {
        this.currency = currency;
        this.mntDev = mntDev != null ? mntDev : BigDecimal.ZERO;
        this.besoinDev = besoinDev != null ? besoinDev : BigDecimal.ZERO;
        this.user = user;
    }

    @PrePersist
    @PreUpdate
    public void applyDynamicScaling() {
        if (currency != null && currency.getNbrDec() != null) {
            int scale = currency.getNbrDec();
            if (mntDev != null) {
                mntDev = mntDev.setScale(scale, BigDecimal.ROUND_HALF_UP);
            }
            if (besoinDev != null) {
                besoinDev = besoinDev.setScale(scale, BigDecimal.ROUND_HALF_UP);
            }
        }
    }
}