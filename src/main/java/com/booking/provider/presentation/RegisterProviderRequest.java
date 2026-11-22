package com.booking.provider.presentation;

import com.booking.provider.application.RegisterProviderCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterProviderRequest(
        @NotBlank(message = "Name is required") @Size(max = 100, message = "Name is too long") String name,
        @Size(max = 500, message = "Description is too long") String description) {

    public RegisterProviderCommand toCommand(Long userId) {
        return new RegisterProviderCommand(userId, name, description);
    }
}