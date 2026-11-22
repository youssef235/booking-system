package com.booking.availability.domain;

import com.booking.shared.domain.DomainException;

public class InvalidAvailabilityException extends DomainException {

    public InvalidAvailabilityException(String message) {
        super(message);
    }
}
