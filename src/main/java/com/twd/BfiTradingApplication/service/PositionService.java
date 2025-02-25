package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.dto.PositionDTO;
import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.entity.Position;
import com.twd.BfiTradingApplication.entity.PositionStatus;
import com.twd.BfiTradingApplication.entity.PositionType;
import com.twd.BfiTradingApplication.repository.CrossParityRepository;
import com.twd.BfiTradingApplication.repository.PositionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class PositionService {
    private final PositionRepository positionRepository;
    private final CrossParityRepository crossParityRepository;

    public PositionService(PositionRepository positionRepository, CrossParityRepository crossParityRepository) {
        this.positionRepository = positionRepository;
        this.crossParityRepository = crossParityRepository;
    }

    public Position openPosition(PositionDTO positionDTO) {
        CrossParity crossParity = crossParityRepository.findById(positionDTO.getCrossParityId())
                .orElseThrow(() -> new RuntimeException("CrossParity not found"));

        Position position = new Position();
        position.setOpenPrice(positionDTO.getOpenPrice());
        position.setVolume(positionDTO.getVolume());
        position.setType(PositionType.valueOf(positionDTO.getType()));
        position.setStatus(PositionStatus.OPEN);
        position.setOpenDate(LocalDateTime.now());
        position.setStopLoss(positionDTO.getStopLoss());
        position.setTakeProfit(positionDTO.getTakeProfit());
        position.setCrossParity(crossParity);
        position.setCurrentPrice(crossParity.getRate());

        calculateProfitLoss(position);
        return positionRepository.save(position);
    }

    public Position closePosition(Integer positionId) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        position.setStatus(PositionStatus.CLOSED);
        position.setCloseDate(LocalDateTime.now());
        position.setCurrentPrice(position.getCrossParity().getRate());
        calculateProfitLoss(position);

        return positionRepository.save(position);
    }

    private void calculateProfitLoss(Position position) {
        Double openPrice = position.getOpenPrice();
        Double currentPrice = position.getCurrentPrice();
        Double volume = position.getVolume();

        Double priceDifference = currentPrice - openPrice;
        if (position.getType() == PositionType.SHORT) {
            priceDifference = -priceDifference;
        }

        position.setProfitLoss(priceDifference * volume);
    }

    public List<Position> getOpenPositions() {
        return positionRepository.findByStatus(PositionStatus.OPEN);
    }

    public void updatePositionPrices() {
        List<Position> openPositions = getOpenPositions();
        for (Position position : openPositions) {
            position.setCurrentPrice(position.getCrossParity().getRate());
            calculateProfitLoss(position);
            checkStopLossAndTakeProfit(position);
            positionRepository.save(position);
        }
    }



    public List<Position> getPositionsForCrossParity(Integer crossParityId) {
        return positionRepository.findByCrossParityPk(crossParityId);
    }

    private void checkStopLossAndTakeProfit(Position position) {
        Double currentPrice = position.getCurrentPrice();
        if (position.getStopLoss() != null) {
            if ((position.getType() == PositionType.LONG && currentPrice <= position.getStopLoss()) ||
                    (position.getType() == PositionType.SHORT && currentPrice >= position.getStopLoss())) {
                closePosition(position.getPk());
            }
        }
        if (position.getTakeProfit() != null) {
            if ((position.getType() == PositionType.LONG && currentPrice >= position.getTakeProfit()) ||
                    (position.getType() == PositionType.SHORT && currentPrice <= position.getTakeProfit())) {
                closePosition(position.getPk());
            }
        }
    }
}