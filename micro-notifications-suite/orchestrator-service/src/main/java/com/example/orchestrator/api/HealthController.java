package com.example.orchestrator.api;

import com.example.orchestrator.StartupInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {
  private final StartupInfo info;

  public HealthController(StartupInfo info){ this.info = info; }

  @GetMapping
  public ResponseEntity<?> health(){
    var body = buildHealth("UP");
    var checks = (List<Map<String, Object>>) body.get("checks");
    checks.add(Map.of("data", Map.of("from", info.getStartedAt().toString(), "status", "READY"), "name", "Readiness check", "status", "UP"));
    checks.add(Map.of("data", Map.of("from", info.getStartedAt().toString(), "status", "ALIVE"), "name", "Liveness check", "status", "UP"));
    return ResponseEntity.ok(body);
  }

  @GetMapping("/ready")
  public ResponseEntity<?> ready(){
    var body = buildHealth("UP");
    var checks = (List<Map<String, Object>>) body.get("checks");
    checks.add(Map.of("data", Map.of("from", info.getStartedAt().toString(), "status", "READY"), "name", "Readiness check", "status", "UP"));
    return ResponseEntity.ok(body);
  }

  @GetMapping("/live")
  public ResponseEntity<?> live(){
    var body = buildHealth("UP");
    var checks = (List<Map<String, Object>>) body.get("checks");
    checks.add(Map.of("data", Map.of("from", info.getStartedAt().toString(), "status", "ALIVE"), "name", "Liveness check", "status", "UP"));
    return ResponseEntity.ok(body);
  }

  private Map<String,Object> buildHealth(String status){
    Map<String,Object> body = new HashMap<>();
    body.put("status", status);
    var checks = new java.util.ArrayList<Map<String,Object>>();
    body.put("checks", checks);
    Map<String,Object> details = new HashMap<>();
    details.put("version", info.getVersion());
    details.put("startedAt", info.getStartedAt().toString());
    details.put("uptime", info.getUptime());
    body.put("details", details);
    return body;
  }
}
