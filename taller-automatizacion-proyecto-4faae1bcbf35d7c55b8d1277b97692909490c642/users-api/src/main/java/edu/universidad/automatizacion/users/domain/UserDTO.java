package edu.universidad.automatizacion.users.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDTO(
        String id,
        @NotBlank @Size(min = 3, max = 50) String name,
        @Email String email
) {}
