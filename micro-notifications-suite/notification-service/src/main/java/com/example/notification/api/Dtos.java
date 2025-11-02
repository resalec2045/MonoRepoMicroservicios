package com.example.notification.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.Map;

 record SendRequest(
  @NotNull List<String> channels,
  String recipientEmail,
  String recipientPhone,
  @NotBlank String template,
  Map<String, String> variables
){}

 record ScheduleRequest(
  @NotNull List<String> channels,
  String recipientEmail,
  String recipientPhone,
  @NotBlank String template,
  Map<String, String> variables,
  @NotNull Instant sendAt
){}

 record NotificationResponse(String id, String status){}
