package com.booking.service.presentation;

import com.booking.service.application.CreateServiceCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Duration;

public record CreateServiceRequest(
        @NotBlank(message = "Name is required") @Size(max = 100, message = "Name is too long") String name,
        @Size(max = 500, message = "Description is too long") String description,
        @NotNull(message = "Price is required") @DecimalMin(value = "0.00", message = "Price must not be negative") BigDecimal price,
        @NotNull(message = "Duration is required") @Positive(message = "Duration must be positive") Integer durationMinutes) {

    public CreateServiceCommand toCommand(Long userId) {
        return new CreateServiceCommand(userId, name, description, price, Duration.ofMinutes(durationMinutes));
    }
}