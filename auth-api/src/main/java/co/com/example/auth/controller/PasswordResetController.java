package co.com.example.auth.controller;

import co.com.example.auth.dto.ErrorResponse;
import co.com.example.auth.dto.PasswordResetConfirm;
import co.com.example.auth.dto.PasswordResetRequest;
import co.com.example.auth.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/password-reset")
@Tag(name = "Password Reset", description = "Flujo de recuperación de contraseña")
public class PasswordResetController {

    private final PasswordResetService svc;

    public PasswordResetController(PasswordResetService svc) {
        this.svc = svc;
    }

    @PostMapping("/request")
    @Operation(summary = "Solicitar restablecimiento (202 neutro)")
    public ResponseEntity<?> request(@Valid @RequestBody PasswordResetRequest body,
                                        HttpServletRequest req) {
        svc.requestReset(body.email(), req.getRemoteAddr(), req.getHeader("User-Agent"));
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/confirm")
    @Operation(summary = "Confirmar restablecimiento con token de un solo uso")
    public ResponseEntity<?> confirm(@Valid @RequestBody PasswordResetConfirm body) {
        try {
            svc.confirmReset(body.token(), body.newPassword());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ErrorResponse.badRequest(e.getMessage());
        }
    }
}
