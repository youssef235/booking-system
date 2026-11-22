package com.booking.user.infrastructure.persistence;

import com.booking.user.domain.User;

final class UserMapper {

    private UserMapper() {
    }

    static UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(user.getId(), user.getName(), user.getEmail(), user.getPasswordHash(),
                user.getRole(), user.isEnabled(), user.getCreatedAt(), user.getUpdatedAt());
    }

    static User toDomain(UserJpaEntity entity) {
        return User.restore(entity.getId(), entity.getName(), entity.getEmail(), entity.getPasswordHash(),
                entity.getRole(), entity.isEnabled(), entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
