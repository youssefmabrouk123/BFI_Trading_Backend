package com.twd.BfiTradingApplication.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for receiving User update requests
 */
@Data
public class UserRequestDTO {
    private String firstName;
    private String lastName;
    private String password;
    // Add other fields that can be updated but NOT authorities/roles
}