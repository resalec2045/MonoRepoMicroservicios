package edu.universidad.automatizacion.users.app;

import edu.universidad.automatizacion.users.domain.InMemoryUserRepository;
import edu.universidad.automatizacion.users.domain.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final InMemoryUserRepository repo = new InMemoryUserRepository();

    public UserDTO create(UserDTO u) {
        return repo.save(u);
    }

    public UserDTO update(String id, UserDTO u) {
        return repo.save(new UserDTO(id, u.name(), u.email()));
    }

    public UserDTO get(String id) {
        return repo.findById(id).orElse(null);
    }

    public Optional<UserDTO> findOptional(String id) {
        return repo.findById(id);
    }

    public List<UserDTO> list() {
        return repo.findAll();
    }

    public boolean delete(String id) {
        return repo.delete(id);
    }
}
