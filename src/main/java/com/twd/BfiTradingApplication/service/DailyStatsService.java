package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.dto.DailyStatsDTO;
import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.entity.DailyStats;
import com.twd.BfiTradingApplication.entity.Quote;
import com.twd.BfiTradingApplication.repository.DailyStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class DailyStatsService {
    @Autowired
    private DailyStatsRepository dailyStatsRepository;


    public void updateDailyStatsFromQuote(Quote quote) {
        CrossParity crossParity = quote.getCrossParity();
        LocalDate today = LocalDate.now();

        Optional<DailyStats> optionalStats = dailyStatsRepository.findByCrossParityAndDate(crossParity, today);
        DailyStats dailyStats;
        if (optionalStats.isPresent()) {
            dailyStats = optionalStats.get();
        } else {
            // Création d'un nouvel enregistrement pour le jour
            dailyStats = new DailyStats();
            dailyStats.setCrossParity(crossParity);
            dailyStats.setDate(today);
            // Initialisation avec la première quote du jour
            dailyStats.setOpenBid(quote.getBidPrice());
            dailyStats.setMaxBid(quote.getBidPrice());
            dailyStats.setMinBid(quote.getBidPrice());
            dailyStats.setAverageBid(quote.getBidPrice());
            dailyStats.setCloseBid(quote.getBidPrice());

            dailyStats.setMaxAsk(quote.getAskPrice());
            dailyStats.setMinAsk(quote.getAskPrice());
            dailyStats.setAverageAsk(quote.getAskPrice());

            dailyStats.setVolume(BigDecimal.ZERO); // À adapter si vous calculez le volume
        }

        // Mise à jour continue à partir de la nouvelle quote
        // La valeur de clôture est toujours la dernière quote reçue
        dailyStats.setCloseBid(quote.getBidPrice());
        // Max/Min
        dailyStats.setMaxBid(quote.getBidPrice().max(dailyStats.getMaxBid()));
        dailyStats.setMinBid(quote.getBidPrice().min(dailyStats.getMinBid()));
        dailyStats.setMaxAsk(quote.getAskPrice().max(dailyStats.getMaxAsk()));
        dailyStats.setMinAsk(quote.getAskPrice().min(dailyStats.getMinAsk()));

        // Pour la moyenne, ici nous utilisons une méthode simplifiée.
        // Remarque : pour une moyenne précise, il faudrait stocker le nombre d'updates ou la somme cumulative.
        dailyStats.setAverageBid(
                dailyStats.getAverageBid().add(quote.getBidPrice()).divide(new BigDecimal("2"), RoundingMode.HALF_UP)
        );
        dailyStats.setAverageAsk(
                dailyStats.getAverageAsk().add(quote.getAskPrice()).divide(new BigDecimal("2"), RoundingMode.HALF_UP)
        );

        dailyStatsRepository.save(dailyStats);
    }



    public List<DailyStatsDTO> getDailyStatsByCrossParityId(Integer crossParityId) {
        return dailyStatsRepository.findByCrossParityPk(crossParityId)
                .stream()
                .map(DailyStatsDTO::new)
                .collect(Collectors.toList());
    }

    public List<DailyStatsDTO> getDailyStatsByCrossParityIdAndDate(Integer crossParityId, LocalDate date) {
        return dailyStatsRepository.findByCrossParityPkAndDate(crossParityId, date)
                .stream()
                .map(DailyStatsDTO::new)
                .collect(Collectors.toList());
    }
}
