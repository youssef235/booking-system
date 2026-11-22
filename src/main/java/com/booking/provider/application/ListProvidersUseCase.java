package com.booking.provider.application;

import com.booking.provider.domain.ProviderRepository;
import java.util.List;

public class ListProvidersUseCase {

    private final ProviderRepository providerRepository;

    public ListProvidersUseCase(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public List<ProviderResult> execute() {
        return providerRepository.findAllAcceptingBookings().stream()
                .map(ProviderResult::from)
                .toList();
    }
}