package com.booking.booking.domain;

import com.booking.shared.domain.DomainException;

public class InvalidBookingTransitionException extends DomainException {

    public InvalidBookingTransitionException(BookingStatus from, BookingStatus to) {
        super("Cannot change booking status from " + from + " to " + to);
    }
}
