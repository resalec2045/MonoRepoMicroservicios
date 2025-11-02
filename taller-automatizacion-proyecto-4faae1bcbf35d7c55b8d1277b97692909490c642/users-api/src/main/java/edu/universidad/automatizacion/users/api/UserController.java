package edu.universidad.automatizacion.users.api;

import edu.universidad.automatizacion.users.app.UserService;
import edu.universidad.automatizacion.users.domain.UserDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserDTO dto) {
        UserDTO saved = service.create(dto);
        return ResponseEntity.created(URI.create("/users/" + saved.id())).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        return service.findOptional(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(java.util.Map.of(
                        "message", "User not found",
                        "id", id
                )));
    }

    @GetMapping
    public List<UserDTO> list() {
        return service.list();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable("id") String id, @Valid @RequestBody UserDTO dto) {
        return service.findOptional(id)
                .map(existing -> ResponseEntity.ok(service.update(id, dto)))
                .orElseGet(() -> ResponseEntity.status(404).body((UserDTO) null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
