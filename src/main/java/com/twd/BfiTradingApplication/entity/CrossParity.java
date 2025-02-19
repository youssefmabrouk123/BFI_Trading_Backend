package com.twd.SpringSecurityJWT.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "cross_parities")
public class CrossParity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, unique = true)
    private String symbol;

    // Référence vers la devise de base
    @ManyToOne
    @JoinColumn(name = "base_currency_id", nullable = false)
    private Currency baseCurrency;

    // Référence vers la devise quote
    @ManyToOne
    @JoinColumn(name = "quote_currency_id", nullable = false)
    private Currency quoteCurrency;

    // Relation One-to-One avec Quote (quote instantanée)
    @OneToOne(mappedBy = "crossParity", cascade = CascadeType.ALL)
    private Quote quote;

    // Relation One-to-Many avec QuoteHistory (historique des quotes)
    @OneToMany(mappedBy = "crossParity", cascade = CascadeType.ALL)
    private List<QuoteHistory> quoteHistories = new ArrayList<>();

    // Constructeurs
    public CrossParity() {
    }

    public CrossParity(String description, String symbol, Currency baseCurrency, Currency quoteCurrency) {
        this.description = description;
        this.symbol = symbol;
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Currency getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(Currency quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
        if (quote != null) {
            quote.setCrossParity(this);
        }
    }

    public List<QuoteHistory> getQuoteHistories() {
        return quoteHistories;
    }

    public void setQuoteHistories(List<QuoteHistory> quoteHistories) {
        this.quoteHistories = quoteHistories;
    }

    public void addQuoteHistory(QuoteHistory quoteHistory) {
        this.quoteHistories.add(quoteHistory);
        quoteHistory.setCrossParity(this);
    }
}