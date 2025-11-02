package com.example.auth.events;

public record AuthEvent(String type, String email, String phone, String payload) {}

