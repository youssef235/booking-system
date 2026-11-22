package com.booking.provider.application;

import com.booking.provider.domain.ProviderRepository;
import com.booking.shared.domain.NotFoundException;

public class GetMyProviderUseCase {

    private final ProviderRepository providerRepository;

    public GetMyProviderUseCase(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public ProviderResult execute(Long userId) {
        return providerRepository.findByUserId(userId)
                .map(ProviderResult::from)
                .orElseThrow(() -> new NotFoundException("Provider profile not found"));
    }
}