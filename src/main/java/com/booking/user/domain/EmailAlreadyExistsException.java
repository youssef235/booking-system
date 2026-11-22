package com.booking.user.domain;

import com.booking.shared.domain.DomainException;

public class EmailAlreadyExistsException extends DomainException {

    public EmailAlreadyExistsException(String email) {
        super("Email is already registered: " + email);
    }
}
