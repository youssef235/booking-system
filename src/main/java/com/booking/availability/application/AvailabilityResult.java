package com.booking.availability.application;

import com.booking.availability.domain.Availability;
import com.booking.availability.domain.AvailabilityStatus;
import java.time.Instant;

public record AvailabilityResult(Long id, Long providerId, Instant startTime, Instant endTime,
                                 AvailabilityStatus status) {

    public static AvailabilityResult from(Availability availability) {
        return new AvailabilityResult(availability.getId(), availability.getProviderId(),
                availability.getStartTime(), availability.getEndTime(), availability.getStatus());
    }
}