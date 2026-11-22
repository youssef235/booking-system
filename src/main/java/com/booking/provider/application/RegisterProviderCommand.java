package com.booking.provider.application;

public record RegisterProviderCommand(Long userId, String name, String description) {
}