package com.booking.user.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BcryptPasswordHasherTest {

    private final BcryptPasswordHasher hasher = new BcryptPasswordHasher();

    @Test
    void hashIsNotThePlainPasswordAndMatchesIt() {
        String hash = hasher.hash("password123");

        assertNotEquals("password123", hash);
        assertTrue(hasher.matches("password123", hash));
    }

    @Test
    void wrongPasswordDoesNotMatch() {
        String hash = hasher.hash("password123");

        assertFalse(hasher.matches("wrong-password", hash));
    }

    @Test
    void samePasswordProducesDifferentHashesBecauseOfSalt() {
        assertNotEquals(hasher.hash("password123"), hasher.hash("password123"));
    }
}
