package com.twd.BfiTradingApplication.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "cross_paritie")
public class CrossParity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, unique = true)
    private String identifier;

    // Référence vers la devise de base
    @ManyToOne
    @JoinColumn(name = "base_currency_id", nullable = false)
    private Currency baseCurrency;

    // Référence vers la devise quote
    @ManyToOne
    @JoinColumn(name = "quote_currency_id", nullable = false)
    private Currency quoteCurrency;


    @JsonManagedReference
    @OneToMany(mappedBy = "crossParity", fetch = FetchType.EAGER)
    private List<DailyStats> dailyStats = new ArrayList<>();



    // Relation One-to-One avec Quote (quote instantanée)
    @OneToOne(mappedBy = "crossParity", cascade = CascadeType.ALL)
    @JsonIgnore
    private Quote quote;

    // Relation One-to-Many avec QuoteHistory (historique des quotes)
    @JsonIgnore
    @OneToMany(mappedBy = "crossParity", cascade = CascadeType.ALL)
    private List<QuoteHistory> quoteHistories = new ArrayList<>();


    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal quotity;



    @Column(nullable = false)
    private Double rate; // Add rate field



    @Column(nullable = false)
    private boolean favorite = false;



    @JsonIgnore
    @OneToMany(mappedBy = "crossParity", cascade = CascadeType.ALL)
    private List<Position> positions = new ArrayList<>();

    public boolean isFavorite() {
        return favorite;
    }


    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    // Constructeurs
    public CrossParity() {
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public CrossParity(String description, String identifier, Currency baseCurrency, Currency quoteCurrency, Double rate ,BigDecimal quotity) {
        this.description = description;
        this.identifier = identifier;
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.rate = rate;
        this.quotity=quotity;


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


    public List<DailyStats> getDailyStats() {
        return dailyStats;
    }

    public void setDailyStats(List<DailyStats> dailyStats) {
        this.dailyStats = dailyStats;
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

    public BigDecimal getQuotity() {
        return quotity;
    }

    public void setQuotity(BigDecimal quotity) {
        this.quotity = quotity;
    }
}