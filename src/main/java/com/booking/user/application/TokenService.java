package com.booking.user.application;

import com.booking.user.domain.UserRole;

public interface TokenService {

    IssuedToken generate(Long userId, String email, UserRole role);
}
