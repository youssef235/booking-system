package com.booking.provider.application;

import com.booking.provider.domain.Provider;
import com.booking.provider.domain.ProviderRepository;
import com.booking.shared.domain.ConflictException;
import java.time.Clock;

public class RegisterProviderUseCase {

    private final ProviderRepository providerRepository;
    private final Clock clock;

    public RegisterProviderUseCase(ProviderRepository providerRepository, Clock clock) {
        this.providerRepository = providerRepository;
        this.clock = clock;
    }

    public ProviderResult execute(RegisterProviderCommand command) {
        if (providerRepository.existsByUserId(command.userId())) {
            throw new ConflictException("PROVIDER_ALREADY_EXISTS", "You already have a provider profile");
        }
        Provider provider = Provider.register(command.userId(), command.name(), command.description(),
                clock.instant());
        return ProviderResult.from(providerRepository.save(provider));
    }
}