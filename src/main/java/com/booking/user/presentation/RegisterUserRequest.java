package com.booking.user.presentation;

import com.booking.user.application.RegisterUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Email is required") @Email(message = "Email is not valid") String email,
        @NotBlank(message = "Password is required") String password) {

    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(name, email, password);
    }

    @Override
    public String toString() {
        return "RegisterUserRequest[name=" + name + ", email=" + email + "]";
    }
}
