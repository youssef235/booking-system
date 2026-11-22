package com.booking.availability.application;

import com.booking.availability.domain.Availability;
import com.booking.availability.domain.AvailabilityRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

public class ListProviderAvailabilitiesUseCase {

    private final AvailabilityRepository availabilityRepository;
    private final Clock clock;

    public ListProviderAvailabilitiesUseCase(AvailabilityRepository availabilityRepository, Clock clock) {
        this.availabilityRepository = availabilityRepository;
        this.clock = clock;
    }

    public List<AvailabilityResult> execute(Long providerId) {
        Instant now = clock.instant();
        return availabilityRepository.findByProviderId(providerId).stream()
                .filter(Availability::isOpen)
                .filter(a -> a.getEndTime().isAfter(now))
                .map(AvailabilityResult::from)
                .toList();
    }
}