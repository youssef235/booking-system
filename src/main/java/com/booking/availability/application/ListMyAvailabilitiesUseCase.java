package com.booking.availability.application;

import com.booking.availability.domain.AvailabilityRepository;
import com.booking.provider.application.ProviderResolver;
import java.util.List;

public class ListMyAvailabilitiesUseCase {

    private final AvailabilityRepository availabilityRepository;
    private final ProviderResolver providerResolver;

    public ListMyAvailabilitiesUseCase(AvailabilityRepository availabilityRepository,
                                       ProviderResolver providerResolver) {
        this.availabilityRepository = availabilityRepository;
        this.providerResolver = providerResolver;
    }

    public List<AvailabilityResult> execute(Long userId) {
        Long providerId = providerResolver.requireProvider(userId).getId();
        return availabilityRepository.findByProviderId(providerId).stream()
                .map(AvailabilityResult::from)
                .toList();
    }
}