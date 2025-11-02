package com.example.auth.api;

import com.example.auth.events.EventPublisher;
import com.example.auth.jwt.JwtService;
import com.example.auth.user.User;
import com.example.auth.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private static final Logger log = LoggerFactory.getLogger(AuthController.class);
  private final UserRepository users;
  private final JwtService jwt;
  private final EventPublisher publisher;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public AuthController(UserRepository users, JwtService jwt, EventPublisher publisher) {
    this.users = users; this.jwt = jwt; this.publisher = publisher;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req){
    log.info("register request for email={}", req.email());
    if(users.findByEmail(req.email()).isPresent()) return ResponseEntity.badRequest().body(Map.of("error","exists"));
    User u = new User(); u.setEmail(req.email()); u.setPasswordHash(encoder.encode(req.password())); u.setPhone(req.phone());
    users.save(u);
    publisher.publish("user.registered", u.getEmail(), u.getPhone(), "{}");
    log.info("user registered email={}", u.getEmail());
    return ResponseEntity.ok(Map.of("message","registered"));
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req){
    log.info("login attempt for email={}", req.email());
    var u = users.findByEmail(req.email()).orElse(null);
    if(u==null || !encoder.matches(req.password(), u.getPasswordHash())){
      log.warn("login failed for email={}", req.email());
      return ResponseEntity.status(401).build();
    }
    publisher.publish("user.loggedin", u.getEmail(), u.getPhone(), "{}");
    log.info("user logged in email={}", u.getEmail());
    return ResponseEntity.ok(new TokenResponse(jwt.createToken(u.getEmail())));
  }

  @PostMapping("/password/reset/request")
  public ResponseEntity<?> reset(@Valid @RequestBody PasswordResetRequest req){
    log.info("password reset requested for email={}", req.email());
    var u = users.findByEmail(req.email()).orElse(null);
    if(u!=null) {
      publisher.publish("user.password.reset.requested", u.getEmail(), u.getPhone(), "{}");
      log.info("password reset event published for email={}", u.getEmail());
    }
    return ResponseEntity.ok(Map.of("message","if exists, email sent"));
  }

  @PostMapping("/password/update")
  public ResponseEntity<?> update(@RequestHeader("X-User-Email") String email, @Valid @RequestBody PasswordUpdateRequest req){
    log.info("password update for email={}", email);
    var u = users.findByEmail(email).orElse(null);
    if(u==null) {
      log.warn("password update failed: user not found email={}", email);
      return ResponseEntity.status(401).build();
    }
    if(!new BCryptPasswordEncoder().matches(req.oldPassword(), u.getPasswordHash())) {
      log.warn("password update failed: old password mismatch email={}", email);
      return ResponseEntity.status(403).build();
    }
    u.setPasswordHash(new BCryptPasswordEncoder().encode(req.newPassword())); users.save(u);
    publisher.publish("user.password.updated", u.getEmail(), u.getPhone(), "{}");
    log.info("password updated for email={}", email);
    return ResponseEntity.ok(Map.of("message","updated"));
  }
}
