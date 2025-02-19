package com.twd.SpringSecurityJWT.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "currencies")
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

    public Currency(String name, String symbol, Integer nbrDec) {
        this.description = description;
        this.identifier = identifier;
        this.nbrDec = nbrDec;
    }

    // Getters & Setters
    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getNbrDec() {
        return nbrDec;
    }

    public void setNbrDec(Integer nbrDec) {
        this.nbrDec = nbrDec;
    }
}