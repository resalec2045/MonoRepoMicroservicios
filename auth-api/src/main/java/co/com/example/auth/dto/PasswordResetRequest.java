package co.com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "PasswordResetRequest")
public record PasswordResetRequest(
        @NotBlank @Email String email
) {}
