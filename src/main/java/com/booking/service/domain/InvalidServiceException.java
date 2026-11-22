package com.booking.service.domain;

import com.booking.shared.domain.DomainException;

public class InvalidServiceException extends DomainException {

    public InvalidServiceException(String message) {
        super(message);
    }
}
