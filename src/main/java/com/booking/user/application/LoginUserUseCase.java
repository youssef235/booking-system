package com.booking.user.application;

import com.booking.user.domain.InvalidCredentialsException;
import com.booking.user.domain.User;
import com.booking.user.domain.UserRepository;
import java.util.Locale;
import java.util.Optional;

public class LoginUserUseCase {

    private static final String DUMMY_HASH = "$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5BEBlEzXfS4Nn1bC3cYtXo7xJ0K2u";

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;

    public LoginUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher,
                            TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
    }

    public LoginResult execute(LoginUserCommand command) {
        String email = normalize(command.email());
        String password = command.password() == null ? "" : command.password();

        Optional<User> found = email.isEmpty() ? Optional.empty() : userRepository.findByEmail(email);

        if (found.isEmpty()) {
            passwordHasher.matches(password, DUMMY_HASH);
            throw new InvalidCredentialsException();
        }

        User user = found.get();
        boolean passwordOk = passwordHasher.matches(password, user.getPasswordHash());
        if (!passwordOk || !user.isEnabled()) {
            throw new InvalidCredentialsException();
        }

        IssuedToken issued = tokenService.generate(user.getId(), user.getEmail(), user.getRole());
        return new LoginResult(issued.token(), issued.expiresAt(), UserResult.from(user));
    }

    private static String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
