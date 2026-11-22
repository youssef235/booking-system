package com.booking.booking.application;

import java.time.Instant;

public record CreateBookingCommand(Long userId, Long serviceId, Instant startTime) {
}