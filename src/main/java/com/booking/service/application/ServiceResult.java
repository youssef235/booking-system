package com.booking.service.application;

import com.booking.service.domain.ServiceOffering;
import java.math.BigDecimal;

public record ServiceResult(Long id, Long providerId, String name, String description, BigDecimal price,
                            long durationMinutes, boolean active) {

    public static ServiceResult from(ServiceOffering service) {
        return new ServiceResult(service.getId(), service.getProviderId(), service.getName(),
                service.getDescription(), service.getPrice(), service.getDuration().toMinutes(),
                service.isActive());
    }
}