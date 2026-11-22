package com.booking.booking.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {

    Booking save(Booking booking);

    Optional<Booking> findById(Long id);

    List<Booking> findByUserId(Long userId);

    List<Booking> findByProviderId(Long providerId);

    boolean existsOverlapping(Long providerId, Instant start, Instant end);
}
