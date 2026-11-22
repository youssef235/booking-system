package com.booking.user.application;

import com.booking.user.domain.User;
import com.booking.user.domain.UserRole;

public record UserResult(Long id, String name, String email, UserRole role, boolean enabled) {

    public static UserResult from(User user) {
        return new UserResult(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isEnabled());
    }
}
