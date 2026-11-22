package com.booking.user.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.booking.user.domain.User;
import com.booking.user.domain.UserRepository;
import com.booking.user.domain.UserRole;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserRepositoryAdapterIntegrationTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    @Autowired
    private UserRepository userRepository;

    private User newUser(String email) {
        return User.register("Youssef", email, "hash-value", UserRole.USER, NOW);
    }

    @Test
    void savedUserGetsIdAndCanBeFoundByIdAndEmail() {
        User saved = userRepository.save(newUser("adapter-test@mail.com"));

        assertNotNull(saved.getId());
        User byId = userRepository.findById(saved.getId()).orElseThrow();
        User byEmail = userRepository.findByEmail("adapter-test@mail.com").orElseThrow();
        assertEquals(saved.getId(), byId.getId());
        assertEquals(saved.getId(), byEmail.getId());
        assertEquals("Youssef", byEmail.getName());
        assertEquals(UserRole.USER, byEmail.getRole());
        assertTrue(byEmail.isEnabled());
        assertEquals(NOW, byEmail.getCreatedAt());
    }

    @Test
    void existsByEmailReflectsStoredUsers() {
        userRepository.save(newUser("exists-test@mail.com"));

        assertTrue(userRepository.existsByEmail("exists-test@mail.com"));
        assertFalse(userRepository.existsByEmail("missing-test@mail.com"));
    }

    @Test
    void findByEmailReturnsEmptyWhenMissing() {
        assertTrue(userRepository.findByEmail("nobody-test@mail.com").isEmpty());
    }

    @Test
    void databaseRejectsDuplicateEmail() {
        userRepository.save(newUser("duplicate-test@mail.com"));

        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.save(newUser("duplicate-test@mail.com")));
    }
}
