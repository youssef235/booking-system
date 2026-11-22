package com.booking.booking.application;

import com.booking.booking.domain.Booking;
import com.booking.booking.domain.BookingStatus;
import java.time.Instant;

public record BookingResult(Long id, Long userId, Long serviceId, Long providerId, Instant startTime,
                            Instant endTime, BookingStatus status, String serviceName, String providerName,
                            String customerName) {

    public static BookingResult from(Booking booking) {
        return from(booking, null, null, null);
    }

    public static BookingResult from(Booking booking, String serviceName, String providerName) {
        return from(booking, serviceName, providerName, null);
    }

    public static BookingResult from(Booking booking, String serviceName, String providerName,
                                     String customerName) {
        return new BookingResult(booking.getId(), booking.getUserId(), booking.getServiceId(),
                booking.getProviderId(), booking.getStartTime(), booking.getEndTime(), booking.getStatus(),
                serviceName, providerName, customerName);
    }
}