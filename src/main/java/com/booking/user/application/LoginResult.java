package com.booking.user.application;

public record LoginResult(String accessToken, java.time.Instant expiresAt, UserResult user) {

    @Override
    public String toString() {
        return "LoginResult[expiresAt=" + expiresAt + ", user=" + user + "]";
    }
}
