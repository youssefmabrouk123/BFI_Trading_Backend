//package com.twd.BfiTradingApplication.entity;
//import jakarta.persistence.*;
//import lombok.Data;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "transaction")
//@Data
//public class Transaction {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
//    @ManyToOne
//    @JoinColumn(name = "dev_achn_id", nullable = false)
//    private Currency devAchn;
//
//    @ManyToOne
//    @JoinColumn(name = "dev_ven_id", nullable = false)
//    private Currency devVen;
//
//    @Column(name = "mnt_acht", precision = 19, scale = 4, nullable = false)
//    private BigDecimal mntAcht;
//
//    @Column(name = "mnt_ven", precision = 19, scale = 4, nullable = false)
//    private BigDecimal mntVen;
//
//    @Column(name = "price", precision = 19, scale = 4, nullable = false)
//    private BigDecimal price;
//
//    @Column(name = "transaction_time", nullable = false)
//    private LocalDateTime transactionTime = LocalDateTime.now();
//
//    @Column(name = "transaction_type", nullable = false)
//    private String transactionType;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user; // Utilisateur effectuant la transaction
//
//    public Transaction() {
//        this.mntAcht = BigDecimal.ZERO;
//        this.mntVen = BigDecimal.ZERO;
//        this.price = BigDecimal.ZERO;
//        this.transactionType = "BUY";
//    }
//
//    public Transaction(Currency devAchn, Currency devVen, BigDecimal mntAcht,
//                       BigDecimal mntVen, BigDecimal price, String transactionType, User user) {
//        this.devAchn = devAchn;
//        this.devVen = devVen;
//        this.mntAcht = mntAcht != null ? mntAcht : BigDecimal.ZERO;
//        this.mntVen = mntVen != null ? mntVen : BigDecimal.ZERO;
//        this.price = price != null ? price : BigDecimal.ZERO;
//        this.transactionType = transactionType != null ? transactionType : "BUY";
//        this.user = user;
//    }
//}


package com.twd.BfiTradingApplication.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "dev_achn_id", nullable = false)
    private Currency devAchn;

    @ManyToOne
    @JoinColumn(name = "dev_ven_id", nullable = false)
    private Currency devVen;

    @Column(name = "mnt_acht", precision = 19, scale = 4, nullable = false)
    private BigDecimal mntAcht;

    @Column(name = "mnt_ven", precision = 19, scale = 4, nullable = false)
    private BigDecimal mntVen;

    @Column(name = "price", precision = 19, scale = 4, nullable = false)
    private BigDecimal price;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime = LocalDateTime.now();

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "counterparty_id", nullable = false)
    private Counterparty counterparty;


    @Column(name = "dateValue", nullable = false)
   private LocalDate dateValue;

    public Transaction() {
        this.mntAcht = BigDecimal.ZERO;
        this.mntVen = BigDecimal.ZERO;
        this.price = BigDecimal.ZERO;
        this.transactionType = "BUY";
    }

    public Transaction(Currency devAchn, Currency devVen, BigDecimal mntAcht,
                       BigDecimal mntVen, BigDecimal price, String transactionType,
                       User user, Counterparty counterparty,LocalDate dateValue) {
        this.devAchn = devAchn;
        this.devVen = devVen;
        this.mntAcht = mntAcht != null ? mntAcht : BigDecimal.ZERO;
        this.mntVen = mntVen != null ? mntVen : BigDecimal.ZERO;
        this.price = price != null ? price : BigDecimal.ZERO;
        this.transactionType = transactionType != null ? transactionType : "BUY";
        this.user = user;
        this.counterparty = counterparty;
        this.dateValue = dateValue;
    }
}
