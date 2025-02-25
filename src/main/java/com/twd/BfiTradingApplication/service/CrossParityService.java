package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.entity.CrossParity;

import java.util.List;

public interface CrossParityService {
    CrossParity createCrossParity(CrossParity crossParity);
    CrossParity updateCrossParity(Integer id, CrossParity crossParity);
    CrossParity getCrossParityById(Integer id);
    void deleteCrossParity(Integer id);
    List<CrossParity> getAllCrossParities();
//    void populateCrossParities();
    CrossParity updateFavorie(Integer id, boolean favorite);
    List<String> getAllCrossParityIdentifiers();

    }