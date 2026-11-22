package com.booking.user.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.booking.user.domain.EmailAlreadyExistsException;
import com.booking.user.domain.User;
import com.booking.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class RegisterUserIntegrationTest {

    @Autowired
    private RegisterUserUseCase registerUserUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordHasher passwordHasher;

    @Test
    void registersUserAndStoresBcryptHashOnly() {
        UserResult result = registerUserUseCase.execute(
                new RegisterUserCommand("Youssef", "register-it@mail.com", "password123"));

        assertNotNull(result.id());
        User stored = userRepository.findById(result.id()).orElseThrow();
        assertNotEquals("password123", stored.getPasswordHash());
        assertTrue(stored.getPasswordHash().startsWith("$2"));
        assertTrue(passwordHasher.matches("password123", stored.getPasswordHash()));
        assertFalse(passwordHasher.matches("wrong-password", stored.getPasswordHash()));
    }

    @Test
    void registeringSameEmailTwiceIsRejected() {
        registerUserUseCase.execute(new RegisterUserCommand("Youssef", "twice-it@mail.com", "password123"));

        assertThrows(EmailAlreadyExistsException.class, () -> registerUserUseCase.execute(
                new RegisterUserCommand("Another", "TWICE-IT@mail.com", "password456")));
    }
}
