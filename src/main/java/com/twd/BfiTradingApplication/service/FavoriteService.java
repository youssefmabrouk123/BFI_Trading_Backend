package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.entity.User;
import com.twd.BfiTradingApplication.repository.CrossParityRepository;
import com.twd.BfiTradingApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CrossParityRepository crossParityRepository;

    @Transactional
    public void toggleFavorite(Integer crossParityId) {
        User user = getAuthenticatedUser();
        CrossParity crossParity = crossParityRepository.findById(crossParityId)
                .orElseThrow(() -> new RuntimeException("CrossParity not found"));

        List<CrossParity> favorites = user.getFavoriteCrossParities();
        if (favorites.contains(crossParity)) {
            favorites.remove(crossParity); // Retirer des favoris
        } else {
            favorites.add(crossParity); // Ajouter aux favoris
        }
        userRepository.save(user);
    }

    public List<CrossParity> getUserFavorites() {
        User user = getAuthenticatedUser();
        return user.getFavoriteCrossParities();
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}