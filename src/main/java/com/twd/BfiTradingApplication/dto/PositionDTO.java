package com.twd.BfiTradingApplication.dto;


import com.twd.BfiTradingApplication.entity.User;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class PositionDTO {
    private Integer pk;
    private Integer currencyId; // Référence à l'ID de la devise
    private String identifier;
    private BigDecimal mntDev;
    private BigDecimal besoinDev;
    private User user;
}