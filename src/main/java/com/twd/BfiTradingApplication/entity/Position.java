    package com.twd.BfiTradingApplication.entity;

    import jakarta.persistence.*;
    import lombok.Data;

    import java.math.BigDecimal;
    import java.time.LocalDateTime;
    @Data
    @Entity
    @Table(name = "position")
    public class Position {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer pk;

        @Column(nullable = false)
        private Double openPrice;

        @Column
        private Double currentPrice;

        @Column(nullable = false)
        private Double volume;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private PositionType type;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private PositionStatus status;

        @Column(nullable = false)
        private LocalDateTime openDate;

        private LocalDateTime closeDate;

        @Column
        private Double profitLoss;

        @Column
        private Double stopLoss;

        @Column
        private Double takeProfit;

        @ManyToOne
        @JoinColumn(name = "cross_parity_id", nullable = false)
        private CrossParity crossParity;

        // Constructeur par défaut
        public Position() {}

        // Getters et Setters
        public Integer getPk() {
            return pk;
        }

        public void setPk(Integer pk) {
            this.pk = pk;
        }

        public Double getOpenPrice() {
            return openPrice;
        }

        public void setOpenPrice(Double openPrice) {
            this.openPrice = openPrice;
        }

        public Double getCurrentPrice() {
            return currentPrice;
        }

        public void setCurrentPrice(Double currentPrice) {
            this.currentPrice = currentPrice;
        }

        public Double getVolume() {
            return volume;
        }

        public void setVolume(Double volume) {
            this.volume = volume;
        }

        public PositionType getType() {
            return type;
        }

        public void setType(PositionType type) {
            this.type = type;
        }

        public PositionStatus getStatus() {
            return status;
        }

        public void setStatus(PositionStatus status) {
            this.status = status;
        }

        public LocalDateTime getOpenDate() {
            return openDate;
        }

        public void setOpenDate(LocalDateTime openDate) {
            this.openDate = openDate;
        }

        public LocalDateTime getCloseDate() {
            return closeDate;
        }

        public void setCloseDate(LocalDateTime closeDate) {
            this.closeDate = closeDate;
        }

        public Double getProfitLoss() {
            return profitLoss;
        }

        public void setProfitLoss(Double profitLoss) {
            this.profitLoss = profitLoss;
        }

        public Double getStopLoss() {
            return stopLoss;
        }

        public void setStopLoss(Double stopLoss) {
            this.stopLoss = stopLoss;
        }

        public Double getTakeProfit() {
            return takeProfit;
        }

        public void setTakeProfit(Double takeProfit) {
            this.takeProfit = takeProfit;
        }

        public CrossParity getCrossParity() {
            return crossParity;
        }

        public void setCrossParity(CrossParity crossParity) {
            this.crossParity = crossParity;
        }
    }