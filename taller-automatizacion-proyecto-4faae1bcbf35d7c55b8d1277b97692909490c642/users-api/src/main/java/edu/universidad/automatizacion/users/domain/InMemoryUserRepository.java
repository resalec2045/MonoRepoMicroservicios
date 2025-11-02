package edu.universidad.automatizacion.users.domain;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository {
    private final Map<String, UserDTO> db = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public UserDTO save(UserDTO u) {
        String id = (u.id() == null || u.id().isBlank()) ? String.valueOf(seq.getAndIncrement()) : u.id();
        UserDTO toSave = new UserDTO(id, u.name(), u.email());
        db.put(id, toSave);
        return toSave;
    }

    public Optional<UserDTO> findById(String id) {
        return Optional.ofNullable(db.get(id));
    }

    public List<UserDTO> findAll() {
        return new ArrayList<>(db.values());
    }

    public boolean delete(String id) {
        return db.remove(id) != null;
    }
}
