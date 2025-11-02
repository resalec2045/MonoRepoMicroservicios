package com.example.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class StartupInfo {
  private final Instant startedAt = Instant.now();

  @Value("${app.version:1.0.0}")
  private String version;

  public Instant getStartedAt(){ return startedAt; }
  public String getVersion(){ return version; }
  public String getUptime(){ return Duration.between(startedAt, Instant.now()).toString(); }
}

