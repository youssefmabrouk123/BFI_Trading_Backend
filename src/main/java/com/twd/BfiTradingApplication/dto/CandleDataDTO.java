package com.twd.BfiTradingApplication.dto;

public class CandleDataDTO {
    private String date;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;

    // Getters and Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getOpen() { return open; }
    public void setOpen(double open) { this.open = open; }

    public double getHigh() { return high; }
    public void setHigh(double high) { this.high = high; }

    public double getLow() { return low; }
    public void setLow(double low) { this.low = low; }

    public double getClose() { return close; }
    public void setClose(double close) { this.close = close; }

    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }
}