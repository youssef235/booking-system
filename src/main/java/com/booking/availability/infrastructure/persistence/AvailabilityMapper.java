package com.booking.availability.infrastructure.persistence;

import com.booking.availability.domain.Availability;

final class AvailabilityMapper {

    private AvailabilityMapper() {
    }

    static AvailabilityJpaEntity toEntity(Availability availability) {
        return new AvailabilityJpaEntity(availability.getId(), availability.getProviderId(),
                availability.getStartTime(), availability.getEndTime(), availability.getStatus(),
                availability.getCreatedAt(), availability.getUpdatedAt());
    }

    static Availability toDomain(AvailabilityJpaEntity entity) {
        return Availability.restore(entity.getId(), entity.getProviderId(), entity.getStartTime(),
                entity.getEndTime(), entity.getStatus(), entity.getCreatedAt(), entity.getUpdatedAt());
    }
}