package com.booking.availability.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository {

    Availability save(Availability availability);

    Optional<Availability> findById(Long id);

    List<Availability> findByProviderId(Long providerId);

    boolean existsOverlapping(Long providerId, Instant start, Instant end);

    void deleteById(Long id);
}
