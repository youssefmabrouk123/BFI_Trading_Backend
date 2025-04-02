    package com.twd.BfiTradingApplication.entity;

    import jakarta.persistence.*;
    import lombok.Data;

    @Data
    @Entity
    @Table(name = "currencie")
    public class Currency {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer pk;

        @Column(nullable = false)
        private String description;

        @Column(nullable = false, unique = true)
        private String identifier;

        private Integer nbrDec;



        // Constructeurs
        public Currency() {
        }

        public Currency(String description, String identifier, Integer nbrDec) {
            this.description = description;
            this.identifier = identifier;
            this.nbrDec = nbrDec;
        }
    }