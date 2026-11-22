package com.booking.availability.application;

import com.booking.availability.domain.Availability;
import com.booking.availability.domain.AvailabilityRepository;
import com.booking.provider.application.ProviderResolver;
import com.booking.provider.domain.Provider;
import com.booking.shared.domain.ConflictException;
import java.time.Clock;

public class CreateAvailabilityUseCase {

    private final AvailabilityRepository availabilityRepository;
    private final ProviderResolver providerResolver;
    private final Clock clock;

    public CreateAvailabilityUseCase(AvailabilityRepository availabilityRepository,
                                     ProviderResolver providerResolver, Clock clock) {
        this.availabilityRepository = availabilityRepository;
        this.providerResolver = providerResolver;
        this.clock = clock;
    }

    public AvailabilityResult execute(CreateAvailabilityCommand command) {
        Provider provider = providerResolver.requireActiveProvider(command.userId());
        Availability availability = Availability.create(provider.getId(), command.startTime(), command.endTime(),
                clock.instant());
        if (availabilityRepository.existsOverlapping(provider.getId(), command.startTime(), command.endTime())) {
            throw new ConflictException("AVAILABILITY_OVERLAP", "Availability overlaps an existing window");
        }
        return AvailabilityResult.from(availabilityRepository.save(availability));
    }
}