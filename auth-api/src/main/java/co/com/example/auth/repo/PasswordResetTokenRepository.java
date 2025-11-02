package co.com.example.auth.repo;

import co.com.example.auth.model.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByTokenHashAndExpiresAtAfter(String tokenHash, Instant now);
}
