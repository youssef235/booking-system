package com.booking.shared.domain;

public class NotFoundException extends DomainException {

    public NotFoundException(String message) {
        super(message);
    }
}