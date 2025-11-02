package co.com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginResponse")
public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}
