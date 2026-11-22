package com.booking.availability.presentation;

import com.booking.availability.application.CreateAvailabilityCommand;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record CreateAvailabilityRequest(
        @NotNull(message = "Start time is required") Instant startTime,
        @NotNull(message = "End time is required") Instant endTime) {

    public CreateAvailabilityCommand toCommand(Long userId) {
        return new CreateAvailabilityCommand(userId, startTime, endTime);
    }
}