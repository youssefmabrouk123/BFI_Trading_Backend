package com.twd.BfiTradingApplication.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_action")
public class UserAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Utilisateur ayant effectué l'action

    @Column(nullable = false)
    private String actionType; // Ex: "TRANSACTION", "LOGIN"

    @Column
    private String details; // Détails de l'action

    @Column(nullable = false)
    private LocalDateTime actionTime; // Date et heure de l'action

    @Column(precision = 19, scale = 4)
    private BigDecimal amount; // Montant (si applicable)

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency; // Devise associée (si applicable)
}