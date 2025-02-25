package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.dto.QuoteDTO;
import com.twd.BfiTradingApplication.entity.DailyStats;
import com.twd.BfiTradingApplication.entity.Quote;
import com.twd.BfiTradingApplication.repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/api/quotes")
public class QuoteController {

    @Autowired
    private QuoteRepository quoteRepository;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamQuotes() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        final boolean[] completed = {false};
        emitter.onCompletion(() -> {
            completed[0] = true;
            System.out.println("Emitter completed");
        });

        emitter.onError((ex) -> {
            System.err.println("Error occurred: " + ex.getMessage());
            completed[0] = true;
            emitter.complete();
        });

        new Thread(() -> {
            while (!completed[0]) {
                try {
                    List<QuoteDTO> quoteDTOs = convertToDTO(fetchQuotesWithTransaction());
                    emitter.send(
                            SseEmitter.event()
                                    .data(quoteDTOs)
                                    .id(String.valueOf(System.currentTimeMillis()))
                                    .name("quotes")
                    );
                    Thread.sleep(3000);
                } catch (IOException e) {
                    System.err.println("Error sending data: " + e.getMessage());
                    emitter.completeWithError(e);
                    break;
                } catch (InterruptedException e) {
                    System.err.println("Thread interrupted: " + e.getMessage());
                    Thread.currentThread().interrupt();
                    emitter.completeWithError(e);
                    break;
                } catch (Exception e) {
                    System.err.println("Unexpected error: " + e.getMessage());
                    emitter.completeWithError(e);
                    break;
                }
            }
        }).start();

        return emitter;
    }

    @Transactional(readOnly = true)
    protected List<Quote> fetchQuotesWithTransaction() {
        try {
            return quoteRepository.findAllWithDailyStats();
        } catch (Exception e) {
            System.err.println("Error fetching quotes: " + e.getMessage());
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    protected List<QuoteDTO> convertToDTO(List<Quote> quotes) {
        return quotes.stream().map(quote -> {
            QuoteDTO dto = new QuoteDTO();
            dto.setBidPrice(quote.getBidPrice());
            dto.setAskPrice(quote.getAskPrice());
            dto.setSpread(quote.getSpread());
            dto.setNetVar(quote.getNetVar());
            dto.setPercentageVar(quote.getPercentageVar());
            dto.setQuoteTime(quote.getQuoteTime());
            dto.setIdentifier(quote.getCrossParity().getIdentifier());
            dto.setFavorite(quote.getCrossParity().isFavorite());
            dto.setPk(quote.getCrossParity().getPk());

            // Récupérer la liste des DailyStats
            List<DailyStats> dailyStats = quote.getCrossParity().getDailyStats();

            if (!dailyStats.isEmpty()) {
                // Date d'aujourd'hui
                LocalDate today = LocalDate.now();
                // Date d'hier
                LocalDate yesterday = today.minusDays(1);

                // Trouver la DailyStats d'aujourd'hui
                DailyStats todayStats = dailyStats.stream()
                        .filter(ds -> ds.getDate().equals(today))
                        .findFirst()
                        .orElse(null); // Si aucune DailyStats pour aujourd'hui, on renvoie null

                // Trouver la DailyStats d'hier
                DailyStats yesterdayStats = dailyStats.stream()
                        .filter(ds -> ds.getDate().equals(yesterday))
                        .findFirst()
                        .orElse(null); // Si aucune DailyStats pour hier, on renvoie null

                // Remplir les valeurs dans le DTO à partir de DailyStats d'aujourd'hui
                if (todayStats != null) {
                    dto.setMaxAsk(todayStats.getMaxAsk());
                    dto.setMinBid(todayStats.getMinBid());
                }

                // Remplir la valeur de CloseBid à partir de DailyStats d'hier
                if (yesterdayStats != null) {
                    dto.setCloseBid(yesterdayStats.getCloseBid());
                }
            }

            return dto;
        }).collect(Collectors.toList());
    }


}