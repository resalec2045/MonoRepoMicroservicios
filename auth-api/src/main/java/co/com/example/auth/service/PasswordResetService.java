package co.com.example.auth.service;

import co.com.example.auth.model.PasswordResetToken;
import co.com.example.auth.model.User;
import co.com.example.auth.repo.PasswordResetTokenRepository;
import co.com.example.auth.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class PasswordResetService {

    private static final Duration TOKEN_TTL = Duration.ofMinutes(20);
    private static final int MAX_ATTEMPTS = 5;

    private final UserRepository userRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final Mailer mailer;
    private final String serverSecret;
    private final String appUrl;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetService(UserRepository userRepo,
                                PasswordResetTokenRepository tokenRepo,
                                Mailer mailer,
                                @Value("${app.security.server-secret}") String serverSecret,
                                @Value("${app.url}") String appUrl) {
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
        this.mailer = mailer;
        this.serverSecret = serverSecret;
        this.appUrl = appUrl;
    }

    public void requestReset(String email, String ip, String ua) {
        Optional<User> userOpt = userRepo.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) return; // respuesta neutra

        User user = userOpt.get();
        String rawToken = randomToken(32);
        String tokenHash = hmacSha256Hex(rawToken);

        Instant now = Instant.now();
        PasswordResetToken rec = PasswordResetToken.builder()
                .userId(user.getId())
                .tokenHash(tokenHash)
                .purpose("pwd_reset")
                .createdAt(now)
                .expiresAt(now.plus(TOKEN_TTL))
                .usedAt(null)
                .attempts(0)
                .maxAttempts(MAX_ATTEMPTS)
                .ipRequested(ip)
                .uaRequested(ua)
                .build();
        tokenRepo.save(rec);

        String link = appUrl + "/reset-password?token=" + rawToken;
        String body = "Usa este enlace para restablecer tu contraseña (vence en %d minutos):\n%s"
                .formatted(TOKEN_TTL.toMinutes(), link);
        mailer.send(user.getEmail(), "Restablece tu contraseña", body);
    }

    public void confirmReset(String rawToken, String newPassword) {
        String tokenHash = hmacSha256Hex(rawToken);
        PasswordResetToken rec = tokenRepo.findByTokenHashAndExpiresAtAfter(tokenHash, Instant.now())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido o expirado"));

        if (rec.getUsedAt() != null) throw new IllegalStateException("Token ya utilizado");
        if (rec.getAttempts() >= rec.getMaxAttempts()) throw new IllegalStateException("Token bloqueado");

        if (newPassword == null || newPassword.length() < 12)
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 12 caracteres");

        User user = userRepo.findById(rec.getUserId()).orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
        // Actualizar pass
        user.setPasswordHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(12).encode(newPassword));
        user.setUpdatedAt(Instant.now());
        userRepo.save(user);

        rec.setUsedAt(Instant.now());
        tokenRepo.save(rec);
        // TODO: revocar sesiones activas si manejas refresh tokens
    }

    private String randomToken(int byteLen) {
        byte[] buf = new byte[byteLen];
        random.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
        }

    private String hmacSha256Hex(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(serverSecret.getBytes(), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new IllegalStateException("HMAC error", e);
        }
    }
}
