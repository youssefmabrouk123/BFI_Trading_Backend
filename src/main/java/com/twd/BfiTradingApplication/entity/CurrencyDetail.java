package com.twd.BfiTradingApplication.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "currency_detail")
public class CurrencyDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "currency_id", nullable = false, unique = true)
    private Currency currency; // Relation one-to-one avec Currency

    @Column(name = "mntdev")
    private Double mntdev; // Stock actuel de la banque

    @Column(name = "besoin_dev")
    private Double besoinDev; // Montant à acheter ou vendre

    @Column(name = "position_final")
    private Double positionFinal; // Stock final (mntdev + besoin_dev)

    // Constructeur pour calculer position_final
    public CurrencyDetail() {
        updatePositionFinal();
    }
    // Getters et Setters
    public void setMntdev(Double mntdev) {
        this.mntdev = mntdev;
        updatePositionFinal();
    }
    public Double getBesoinDev() { return besoinDev; }
    public void setBesoinDev(Double besoinDev) {
        this.besoinDev = besoinDev;
        updatePositionFinal();
    }
    private void updatePositionFinal() {
        if (mntdev != null && besoinDev != null) {
            this.positionFinal = mntdev + besoinDev;
        }
    }
}