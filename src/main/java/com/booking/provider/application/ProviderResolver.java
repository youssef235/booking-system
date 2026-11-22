package com.booking.provider.application;

import com.booking.provider.domain.Provider;
import com.booking.provider.domain.ProviderRepository;
import com.booking.shared.domain.ForbiddenOperationException;

public class ProviderResolver {

    private final ProviderRepository providerRepository;

    public ProviderResolver(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public Provider requireProvider(Long userId) {
        return providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ForbiddenOperationException("Register as a provider first"));
    }

    public Provider requireActiveProvider(Long userId) {
        Provider provider = requireProvider(userId);
        if (!provider.acceptsBookings()) {
            throw new ForbiddenOperationException("Provider account is not active");
        }
        return provider;
    }
}