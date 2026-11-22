package com.booking.service.application;

import com.booking.service.domain.ServiceOffering;
import com.booking.service.domain.ServiceOfferingRepository;
import java.util.List;

public class ListProviderServicesUseCase {

    private final ServiceOfferingRepository serviceRepository;

    public ListProviderServicesUseCase(ServiceOfferingRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<ServiceResult> execute(Long providerId) {
        return serviceRepository.findByProviderId(providerId).stream()
                .filter(ServiceOffering::isActive)
                .map(ServiceResult::from)
                .toList();
    }
}