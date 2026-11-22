package com.booking.user.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.booking.user.domain.EmailAlreadyExistsException;
import com.booking.user.domain.InvalidUserException;
import com.booking.user.domain.User;
import com.booking.user.domain.UserRepository;
import com.booking.user.domain.UserRole;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class RegisterUserUseCaseTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordHasher passwordHasher = mock(PasswordHasher.class);
    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
    private final RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository, passwordHasher, clock);

    private void stubSaveToAssignId() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            return User.restore(1L, saved.getName(), saved.getEmail(), saved.getPasswordHash(), saved.getRole(),
                    saved.isEnabled(), saved.getCreatedAt(), saved.getUpdatedAt());
        });
    }

    @Test
    void registersNewUserWithHashedPasswordAndUserRole() {
        when(passwordHasher.hash("password123")).thenReturn("hashed-value");
        when(userRepository.existsByEmail("a@b.com")).thenReturn(false);
        stubSaveToAssignId();

        UserResult result = useCase.execute(new RegisterUserCommand("Youssef", "A@B.com", "password123"));

        assertEquals(1L, result.id());
        assertEquals("a@b.com", result.email());
        assertEquals(UserRole.USER, result.role());
        assertTrue(result.enabled());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("hashed-value", captor.getValue().getPasswordHash());
        assertEquals(NOW, captor.getValue().getCreatedAt());
    }

    @Test
    void duplicateEmailFailsAndNothingIsSaved() {
        when(passwordHasher.hash("password123")).thenReturn("hashed-value");
        when(userRepository.existsByEmail("a@b.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> useCase.execute(new RegisterUserCommand("Youssef", "a@b.com", "password123")));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shortPasswordFailsBeforeHashing() {
        assertThrows(InvalidUserException.class,
                () -> useCase.execute(new RegisterUserCommand("Youssef", "a@b.com", "short")));

        verify(passwordHasher, never()).hash(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void tooLongPasswordFails() {
        String tooLong = "a".repeat(73);

        assertThrows(InvalidUserException.class,
                () -> useCase.execute(new RegisterUserCommand("Youssef", "a@b.com", tooLong)));
    }

    @Test
    void invalidEmailFailsAndNothingIsSaved() {
        when(passwordHasher.hash("password123")).thenReturn("hashed-value");

        assertThrows(InvalidUserException.class,
                () -> useCase.execute(new RegisterUserCommand("Youssef", "not-an-email", "password123")));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void commandToStringDoesNotExposePassword() {
        String text = new RegisterUserCommand("Youssef", "a@b.com", "password123").toString();

        assertTrue(text.contains("a@b.com"));
        assertTrue(!text.contains("password123"));
    }
}
