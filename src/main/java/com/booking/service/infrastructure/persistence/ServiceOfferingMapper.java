package com.booking.service.infrastructure.persistence;

import com.booking.service.domain.ServiceOffering;
import java.time.Duration;

final class ServiceOfferingMapper {

    private ServiceOfferingMapper() {
    }

    static ServiceOfferingJpaEntity toEntity(ServiceOffering service) {
        return new ServiceOfferingJpaEntity(service.getId(), service.getProviderId(), service.getName(),
                service.getDescription(), service.getPrice(), service.getDuration().getSeconds(),
                service.isActive(), service.getCreatedAt(), service.getUpdatedAt());
    }

    static ServiceOffering toDomain(ServiceOfferingJpaEntity entity) {
        return ServiceOffering.restore(entity.getId(), entity.getProviderId(), entity.getName(),
                entity.getDescription(), entity.getPrice(), Duration.ofSeconds(entity.getDurationSeconds()),
                entity.isActive(), entity.getCreatedAt(), entity.getUpdatedAt());
    }
}