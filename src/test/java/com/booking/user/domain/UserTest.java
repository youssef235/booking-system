package com.booking.user.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class UserTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant LATER = Instant.parse("2026-01-02T10:00:00Z");

    @Test
    void registerNormalizesDataAndEnablesUser() {
        User user = User.register("  Youssef ", "  Youssef@Mail.COM ", "hash", UserRole.USER, NOW);

        assertEquals("Youssef", user.getName());
        assertEquals("youssef@mail.com", user.getEmail());
        assertTrue(user.isEnabled());
        assertNull(user.getId());
        assertEquals(NOW, user.getCreatedAt());
    }

    @Test
    void registerWithBlankNameFails() {
        assertThrows(InvalidUserException.class,
                () -> User.register("  ", "a@b.com", "hash", UserRole.USER, NOW));
    }

    @Test
    void registerWithInvalidEmailFails() {
        assertThrows(InvalidUserException.class,
                () -> User.register("Youssef", "not-an-email", "hash", UserRole.USER, NOW));
    }

    @Test
    void disableTurnsOffUserAndUpdatesTimestamp() {
        User user = User.register("Youssef", "a@b.com", "hash", UserRole.USER, NOW);

        user.disable(LATER);

        assertFalse(user.isEnabled());
        assertEquals(LATER, user.getUpdatedAt());
        assertEquals(NOW, user.getCreatedAt());
    }
}
