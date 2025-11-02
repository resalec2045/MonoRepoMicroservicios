package com.example.auth.events;

import java.io.Serializable;

public record AuthEvent(String type, String email, String phone, String payload) implements Serializable {}
