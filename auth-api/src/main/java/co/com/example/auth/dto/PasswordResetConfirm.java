package co.com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "PasswordResetConfirm")
public record PasswordResetConfirm(
        @NotBlank String token,
        @NotBlank @Size(min = 12) String newPassword
) {}
