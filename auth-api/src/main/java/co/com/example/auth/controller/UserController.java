package co.com.example.auth.controller;

import co.com.example.auth.dto.ErrorResponse;
import co.com.example.auth.dto.UserCreate;
import co.com.example.auth.dto.UserUpdate;
import co.com.example.auth.model.User;
import co.com.example.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "CRUD de usuarios")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Crear usuario")
    public ResponseEntity<?> create(@Valid @RequestBody UserCreate body) {
        try {
            User u = userService.create(body.email(), body.username(), body.password());
            return ResponseEntity.status(201).body(u);
        } catch (IllegalArgumentException e) {
            return ErrorResponse.badRequest(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Listar usuarios (paginado)")
    public ResponseEntity<?> list(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "username", defaultValue = "") String username
    ) {
        try {
            Page<User> result;
            if (username == null || username.isEmpty()) {
                result = userService.list(page, size);
            } else {
                result = userService.findByUsername(page, size, username);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ErrorResponse.badRequest(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario (email/role)")
    public ResponseEntity<?> update(@PathVariable(name = "id") String id, @Valid @RequestBody UserUpdate body) {
        try {
            return ResponseEntity.ok(userService.update(id, body.email(), body.role()));
        } catch (IllegalArgumentException e) {
            return ErrorResponse.badRequest(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    public ResponseEntity<?> delete(@PathVariable(name = "id") String id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ErrorResponse.badRequest(e.getMessage());
        }
    }
}
