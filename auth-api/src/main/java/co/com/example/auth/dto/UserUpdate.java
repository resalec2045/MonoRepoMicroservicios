package co.com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "UserUpdate")
public record UserUpdate(
        @NotBlank @Email String email,
        @NotBlank String role
) {}
