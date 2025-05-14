package com.twd.BfiTradingApplication.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


@Embeddable
public class QuoteHistoryId implements Serializable {
    private Integer pk;
    private LocalDateTime quoteTime;

    public QuoteHistoryId() {}

    public QuoteHistoryId(Integer pk, LocalDateTime quoteTime) {
        this.pk = pk;
        this.quoteTime = quoteTime;
    }

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public LocalDateTime getQuoteTime() {
        return quoteTime;
    }

    public void setQuoteTime(LocalDateTime quoteTime) {
        this.quoteTime = quoteTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuoteHistoryId that = (QuoteHistoryId) o;
        return Objects.equals(pk, that.pk) && Objects.equals(quoteTime, that.quoteTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pk, quoteTime);
    }
}
