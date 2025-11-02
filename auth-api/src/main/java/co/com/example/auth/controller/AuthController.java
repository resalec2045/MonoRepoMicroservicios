package co.com.example.auth.controller;

import co.com.example.auth.dto.ErrorResponse;
import co.com.example.auth.dto.LoginRequest;
import co.com.example.auth.dto.LoginResponse;
import co.com.example.auth.model.User;
import co.com.example.auth.security.JwtUtil;
import co.com.example.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Auth", description = "Login con JWT (email como identificador)")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Operation(summary = "Login (email + password)")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        log.info("Intento de login para email={}", req.email());

        Optional<User> u = userService.findByEmail(req.email());
        if (u.isEmpty()) {
            log.warn("Login fallido: usuario {} no encontrado", req.email());
            return ErrorResponse.badRequest("Credenciales inválidas");
        }

        User user = u.get();
        var encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(12);
        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            log.warn("Login fallido: password incorrecto para {}", req.email());
            return ErrorResponse.badRequest("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        log.info("Login exitoso para {}", req.email());
        return ResponseEntity.ok(new LoginResponse(token, "Bearer", 3600));
    }
}
