package com.booking.user.application;

import com.booking.user.domain.EmailAlreadyExistsException;
import com.booking.user.domain.InvalidUserException;
import com.booking.user.domain.User;
import com.booking.user.domain.UserRepository;
import com.booking.user.domain.UserRole;
import java.nio.charset.StandardCharsets;
import java.time.Clock;

public class RegisterUserUseCase {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_BYTES = 72;

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final Clock clock;

    public RegisterUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher, Clock clock) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.clock = clock;
    }

    public UserResult execute(RegisterUserCommand command) {
        requireValidPassword(command.password());

        User user = User.register(command.name(), command.email(),
                passwordHasher.hash(command.password()), UserRole.USER, clock.instant());

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        return UserResult.from(userRepository.save(user));
    }

    private static void requireValidPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new InvalidUserException("Password is required");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new InvalidUserException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            throw new InvalidUserException("Password is too long");
        }
    }
}
