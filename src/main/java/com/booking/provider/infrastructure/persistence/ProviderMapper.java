package com.booking.provider.infrastructure.persistence;

import com.booking.provider.domain.Provider;

final class ProviderMapper {

    private ProviderMapper() {
    }

    static ProviderJpaEntity toEntity(Provider provider) {
        return new ProviderJpaEntity(provider.getId(), provider.getUserId(), provider.getName(),
                provider.getDescription(), provider.getStatus(), provider.getCreatedAt(), provider.getUpdatedAt());
    }

    static Provider toDomain(ProviderJpaEntity entity) {
        return Provider.restore(entity.getId(), entity.getUserId(), entity.getName(), entity.getDescription(),
                entity.getStatus(), entity.getCreatedAt(), entity.getUpdatedAt());
    }
}