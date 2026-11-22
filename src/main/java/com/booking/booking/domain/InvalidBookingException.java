package com.booking.booking.domain;

import com.booking.shared.domain.DomainException;

public class InvalidBookingException extends DomainException {

    public InvalidBookingException(String message) {
        super(message);
    }
}
