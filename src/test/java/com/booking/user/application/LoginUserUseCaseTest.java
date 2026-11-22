package com.booking.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.booking.user.domain.InvalidCredentialsException;
import com.booking.user.domain.User;
import com.booking.user.domain.UserRepository;
import com.booking.user.domain.UserRole;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LoginUserUseCaseTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant EXPIRES = Instant.parse("2026-01-01T11:00:00Z");

    private UserRepository userRepository;
    private PasswordHasher passwordHasher;
    private TokenService tokenService;
    private LoginUserUseCase useCase;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordHasher = Mockito.mock(PasswordHasher.class);
        tokenService = Mockito.mock(TokenService.class);
        useCase = new LoginUserUseCase(userRepository, passwordHasher, tokenService);
    }

    private User user(boolean enabled) {
        return User.restore(7L, "Youssef", "youssef@example.com", "hash", UserRole.USER, enabled, NOW, NOW);
    }

    @Test
    void execute_withValidCredentials_returnsTokenAndUser() {
        when(userRepository.findByEmail("youssef@example.com")).thenReturn(Optional.of(user(true)));
        when(passwordHasher.matches("Secret123", "hash")).thenReturn(true);
        when(tokenService.generate(7L, "youssef@example.com", UserRole.USER))
                .thenReturn(new IssuedToken("jwt-token", EXPIRES));

        LoginResult result = useCase.execute(new LoginUserCommand("youssef@example.com", "Secret123"));

        assertThat(result.accessToken()).isEqualTo("jwt-token");
        assertThat(result.expiresAt()).isEqualTo(EXPIRES);
        assertThat(result.user().id()).isEqualTo(7L);
        assertThat(result.user().email()).isEqualTo("youssef@example.com");
    }

    @Test
    void execute_withUnknownEmail_throwsInvalidCredentialsAndNeverIssuesToken() {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new LoginUserCommand("nobody@example.com", "Secret123")))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(passwordHasher).matches(anyString(), anyString());
        Mockito.verifyNoInteractions(tokenService);
    }

    @Test
    void execute_withWrongPassword_throwsInvalidCredentials() {
        when(userRepository.findByEmail("youssef@example.com")).thenReturn(Optional.of(user(true)));
        when(passwordHasher.matches("wrong", "hash")).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(new LoginUserCommand("youssef@example.com", "wrong")))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");

        Mockito.verifyNoInteractions(tokenService);
    }

    @Test
    void execute_withDisabledAccount_throwsSameInvalidCredentials() {
        when(userRepository.findByEmail("youssef@example.com")).thenReturn(Optional.of(user(false)));
        when(passwordHasher.matches("Secret123", "hash")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(new LoginUserCommand("youssef@example.com", "Secret123")))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");

        Mockito.verifyNoInteractions(tokenService);
    }

    @Test
    void execute_normalizesEmailBeforeLookup() {
        when(userRepository.findByEmail("youssef@example.com")).thenReturn(Optional.of(user(true)));
        when(passwordHasher.matches("Secret123", "hash")).thenReturn(true);
        when(tokenService.generate(any(), anyString(), any())).thenReturn(new IssuedToken("t", EXPIRES));

        LoginResult result = useCase.execute(new LoginUserCommand("  Youssef@Example.COM ", "Secret123"));

        assertThat(result.accessToken()).isEqualTo("t");
        verify(userRepository).findByEmail("youssef@example.com");
    }

    @Test
    void execute_withNullEmailOrPassword_throwsInvalidCredentials() {
        assertThatThrownBy(() -> useCase.execute(new LoginUserCommand(null, null)))
                .isInstanceOf(InvalidCredentialsException.class);

        Mockito.verifyNoInteractions(userRepository, tokenService);
    }
}
