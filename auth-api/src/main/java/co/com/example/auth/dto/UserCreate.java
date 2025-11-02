package co.com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "UserCreate")
public record UserCreate(
        @NotBlank @Email String email,
        @NotBlank String username,
        @NotBlank @Size(min = 12) String password
) {}
