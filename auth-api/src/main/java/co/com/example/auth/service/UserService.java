package co.com.example.auth.service;

import co.com.example.auth.model.User;
import co.com.example.auth.repo.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User create(String email, String username, String rawPassword) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email ya registrado");
        }
        User user = User.builder()
                .email(email)
                .username(username)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .roles(Set.of("USER"))
                .enabled(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return userRepository.save(user);
    }

    public Page<User> list(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    public Page<User> findByUsername(int page, int size, String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username, PageRequest.of(page, size));
    }

    public User update(String id, String email, String role) {
        User u = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        u.setEmail(email);
        u.setUpdatedAt(Instant.now());
        // Simple: roles de un solo valor
        u.setRoles(Set.of(role));
        return userRepository.save(u);
    }

    public void delete(String id) {
        if (!userRepository.existsById(id)) throw new IllegalArgumentException("Usuario no encontrado");
        userRepository.deleteById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public void updatePassword(User user, String rawPassword) {
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }
}
