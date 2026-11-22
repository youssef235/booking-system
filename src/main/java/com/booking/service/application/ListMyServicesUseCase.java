package com.booking.service.application;

import com.booking.provider.application.ProviderResolver;
import com.booking.service.domain.ServiceOfferingRepository;
import java.util.List;

public class ListMyServicesUseCase {

    private final ServiceOfferingRepository serviceRepository;
    private final ProviderResolver providerResolver;

    public ListMyServicesUseCase(ServiceOfferingRepository serviceRepository, ProviderResolver providerResolver) {
        this.serviceRepository = serviceRepository;
        this.providerResolver = providerResolver;
    }

    public List<ServiceResult> execute(Long userId) {
        Long providerId = providerResolver.requireProvider(userId).getId();
        return serviceRepository.findByProviderId(providerId).stream().map(ServiceResult::from).toList();
    }
}