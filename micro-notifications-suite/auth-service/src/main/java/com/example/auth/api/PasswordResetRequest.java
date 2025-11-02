package com.example.auth.api;

import jakarta.validation.constraints.Email;

public record PasswordResetRequest(@Email String email) {}

