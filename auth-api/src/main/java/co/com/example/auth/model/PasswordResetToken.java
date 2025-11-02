package co.com.example.auth.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("password_reset_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PasswordResetToken {
    @Id
    private String id;

    private String userId;

    @Indexed
    private String tokenHash; // HMAC-SHA256 hex

    private String purpose; // "pwd_reset"

    @Indexed
    private Instant createdAt;

    @Indexed(expireAfterSeconds = 0)
    private Instant expiresAt;

    private Instant usedAt;

    private int attempts;
    private int maxAttempts;

    private String ipRequested;
    private String uaRequested;
}
