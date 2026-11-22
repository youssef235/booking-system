package com.booking.booking.presentation;

import com.booking.booking.application.CreateBookingCommand;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record CreateBookingRequest(
        @NotNull(message = "Service is required") Long serviceId,
        @NotNull(message = "Start time is required") Instant startTime) {

    public CreateBookingCommand toCommand(Long userId) {
        return new CreateBookingCommand(userId, serviceId, startTime);
    }
}