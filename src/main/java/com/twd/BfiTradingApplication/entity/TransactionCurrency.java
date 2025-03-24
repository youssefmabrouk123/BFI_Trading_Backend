package com.twd.BfiTradingApplication.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transaction_currency")
public class TransactionCurrency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;  // Devise impliquée dans la transaction (e.g., EUR, USD)

    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;  // Référence à la position qui contient l'achat/vente

    @Column(nullable = false)
    private Integer amount;  // Montant de la devise (positif ou négatif)

    @Column(nullable = false)
    private LocalDateTime transactionTime = LocalDateTime.now();
}
