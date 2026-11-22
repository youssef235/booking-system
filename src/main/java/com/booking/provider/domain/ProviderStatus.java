package com.booking.provider.domain;

public enum ProviderStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED;

    public boolean acceptsBookings() {
        return this == ACTIVE;
    }
}
