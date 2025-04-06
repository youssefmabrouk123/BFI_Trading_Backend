package com.twd.BfiTradingApplication.controller;
import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/toggle/{crossParityId}")
    public void toggleFavorite(@PathVariable Integer crossParityId) {
        favoriteService.toggleFavorite(crossParityId);
    }

    @GetMapping
    public List<CrossParity> getUserFavorites() {
        return favoriteService.getUserFavorites();
    }
}