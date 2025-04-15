package com.twd.BfiTradingApplication.dto;

import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.entity.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) for User entity to be used in API responses
 * This avoids Jackson serialization issues with UserDetails interface
 */
@Data
public class UserDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private List<CrossParityDTO> favoriteCrossParities = new ArrayList<>();

    // Static constructor from User entity
    public static UserDTO fromUser(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());

        // Convert cross parities
        List<CrossParityDTO> crossParityDTOs = new ArrayList<>();
        if (user.getFavoriteCrossParities() != null) {
            for (CrossParity crossParity : user.getFavoriteCrossParities()) {
                CrossParityDTO crossParityDTO = new CrossParityDTO();
                crossParityDTO.setId(crossParity.getPk());
                crossParityDTO.setName(crossParity.getIdentifier());
                crossParityDTOs.add(crossParityDTO);
            }
        }
        dto.setFavoriteCrossParities(crossParityDTOs);

        return dto;
    }

    // Inner DTO class for CrossParity
    @Data
    public static class CrossParityDTO {
        private Integer id;
        private String name;
    }
}