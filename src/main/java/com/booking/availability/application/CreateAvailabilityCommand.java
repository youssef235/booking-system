package com.booking.availability.application;

import java.time.Instant;

public record CreateAvailabilityCommand(Long userId, Instant startTime, Instant endTime) {
}