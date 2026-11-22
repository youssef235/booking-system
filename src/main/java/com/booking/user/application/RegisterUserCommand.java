package com.booking.user.application;

public record RegisterUserCommand(String name, String email, String password) {

    @Override
    public String toString() {
        return "RegisterUserCommand[name=" + name + ", email=" + email + "]";
    }
}
