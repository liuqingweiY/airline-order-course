package com.postion.airlineorderbackend.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String role;
}
