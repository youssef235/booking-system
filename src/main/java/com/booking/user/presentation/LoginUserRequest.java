package com.booking.user.presentation;

import com.booking.user.application.LoginUserCommand;
import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest(
        @NotBlank(message = "Email is required") String email,
        @NotBlank(message = "Password is required") String password) {

    public LoginUserCommand toCommand() {
        return new LoginUserCommand(email, password);
    }

    @Override
    public String toString() {
        return "LoginUserRequest[email=" + email + "]";
    }
}