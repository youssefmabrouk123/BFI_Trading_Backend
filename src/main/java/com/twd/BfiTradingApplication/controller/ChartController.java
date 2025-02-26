package com.twd.BfiTradingApplication.controller;
import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.entity.QuoteHistory;
import com.twd.BfiTradingApplication.repository.CrossParityRepository;
import com.twd.BfiTradingApplication.repository.QuoteHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ChartController {

    @Autowired
    private QuoteHistoryRepository quoteHistoryRepo;

    @Autowired
    private CrossParityRepository crossParityRepo;

    // Standard REST endpoint for initial data
    @GetMapping("/public/api/chart/{crossParityId}")
    public List<ChartDataPoint> getChartData(@PathVariable Integer crossParityId) {
        CrossParity crossParity = crossParityRepo.findById(crossParityId)
                .orElseThrow(() -> new RuntimeException("CrossParity not found"));
        LocalDateTime startDate = LocalDateTime.now().minusDays(7); // Les 7 derniers jours
        List<QuoteHistory> quoteHistories = quoteHistoryRepo.findByCrossParityAndPkQuoteTimeAfter(crossParity, startDate);

        return quoteHistories.stream()
                .map(qh -> new ChartDataPoint(qh.getPk().getQuoteTime(), qh.getBidPrice().doubleValue()))
                .collect(Collectors.toList());
    }

    // SSE endpoint for real-time updates
    @GetMapping(value = "/public/api/chart/stream/{crossParityId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPrices(@PathVariable Integer crossParityId) {
        // Create an emitter with a timeout
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // Get the cross parity
        CrossParity crossParity = crossParityRepo.findById(crossParityId)
                .orElseThrow(() -> new RuntimeException("CrossParity not found"));

        // Store the latest event timestamp to avoid duplicates
        AtomicReference<LocalDateTime> lastEventTime = new AtomicReference<>(LocalDateTime.now());

        // Create a scheduled task to send updates
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        // Schedule task that sends price updates
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
            try {
                // Get latest price data
                LocalDateTime endTime = LocalDateTime.now();
                LocalDateTime startTime = lastEventTime.get();

                List<QuoteHistory> latestQuotes = quoteHistoryRepo.findByCrossParityAndPkQuoteTimeBetween(
                        crossParity, startTime, endTime);

                if (!latestQuotes.isEmpty()) {
                    // Sort and get the most recent quote
                    QuoteHistory latestQuote = latestQuotes.stream()
                            .sorted(Comparator.comparing(qh -> qh.getPk().getQuoteTime(), Comparator.reverseOrder()))
                            .findFirst().orElse(null);

                    if (latestQuote != null) {
                        ChartDataPoint dataPoint = new ChartDataPoint(
                                latestQuote.getPk().getQuoteTime(),
                                latestQuote.getBidPrice().doubleValue());

                        // Update last event time
                        lastEventTime.set(latestQuote.getPk().getQuoteTime().plusNanos(1));

                        // Send the update
                        emitter.send(dataPoint);
                    }
                } else {
                    // Send heartbeat to keep connection alive
                    emitter.send(SseEmitter.event().comment("heartbeat").build());
                }
            } catch (IOException e) {
                emitter.completeWithError(e);
                executor.shutdown();
            }
        }, 0, 5, TimeUnit.SECONDS);

        // Handle completion and cleanup
        emitter.onCompletion(() -> {
            future.cancel(true);
            executor.shutdown();
        });

        emitter.onTimeout(() -> {
            future.cancel(true);
            executor.shutdown();
            emitter.complete();
        });

        emitter.onError(e -> {
            future.cancel(true);
            executor.shutdown();
        });

        return emitter;
    }

    // Classe DTO pour le JSON
    public static class ChartDataPoint {
        public LocalDateTime time;
        public double value;

        public ChartDataPoint(LocalDateTime time, double value) {
            this.time = time;
            this.value = value;
        }
    }
}