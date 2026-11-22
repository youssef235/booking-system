package com.booking.booking.application;

import java.time.Instant;

public record SlotResult(Instant startTime, Instant endTime) {
}