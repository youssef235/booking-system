package com.booking.provider.domain;

import com.booking.shared.domain.DomainException;

public class InvalidProviderException extends DomainException {

    public InvalidProviderException(String message) {
        super(message);
    }
}
