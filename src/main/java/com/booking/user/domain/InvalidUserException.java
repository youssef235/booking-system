package com.booking.user.domain;

import com.booking.shared.domain.DomainException;

public class InvalidUserException extends DomainException {

    public InvalidUserException(String message) {
        super(message);
    }
}
