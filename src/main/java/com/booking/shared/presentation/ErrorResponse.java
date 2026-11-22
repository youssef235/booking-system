package com.booking.shared.presentation;

import java.util.List;

public record ErrorResponse(String code, String message, List<FieldViolation> fieldErrors) {

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, List.of());
    }
}
