package com.booking.service.application;

import java.math.BigDecimal;
import java.time.Duration;

public record CreateServiceCommand(Long userId, String name, String description, BigDecimal price,
                                   Duration duration) {
}