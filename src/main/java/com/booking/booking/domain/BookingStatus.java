package com.booking.booking.domain;

public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    REJECTED,
    COMPLETED;

    public boolean canTransitionTo(BookingStatus target) {
        return switch (this) {
            case PENDING -> target == CONFIRMED || target == CANCELLED || target == REJECTED;
            case CONFIRMED -> target == CANCELLED || target == COMPLETED;
            case CANCELLED, REJECTED, COMPLETED -> false;
        };
    }

    public boolean occupiesSlot() {
        return this == PENDING || this == CONFIRMED;
    }
}
