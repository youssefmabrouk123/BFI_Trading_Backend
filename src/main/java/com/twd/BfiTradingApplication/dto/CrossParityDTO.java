package com.twd.BfiTradingApplication.dto;

public class CrossParityDTO {
    private Integer id;
    private String identifier;
    private String description;
    private String baseCurrency;
    private String quoteCurrency;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }

    public String getQuoteCurrency() { return quoteCurrency; }
    public void setQuoteCurrency(String quoteCurrency) { this.quoteCurrency = quoteCurrency; }
}