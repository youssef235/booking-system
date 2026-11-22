package com.booking.user.application;

public record LoginUserCommand(String email, String password) {

    @Override
    public String toString() {
        return "LoginUserCommand[email=" + email + "]";
    }
}
