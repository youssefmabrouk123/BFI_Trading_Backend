package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.entity.Quote;

public interface DailyStatsService {

    void updateDailyStatsFromQuote(Quote quote);
}
