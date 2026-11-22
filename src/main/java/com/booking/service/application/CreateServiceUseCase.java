package com.booking.service.application;

import com.booking.provider.application.ProviderResolver;
import com.booking.provider.domain.Provider;
import com.booking.service.domain.ServiceOffering;
import com.booking.service.domain.ServiceOfferingRepository;
import java.time.Clock;

public class CreateServiceUseCase {

    private final ServiceOfferingRepository serviceRepository;
    private final ProviderResolver providerResolver;
    private final Clock clock;

    public CreateServiceUseCase(ServiceOfferingRepository serviceRepository, ProviderResolver providerResolver,
                                Clock clock) {
        this.serviceRepository = serviceRepository;
        this.providerResolver = providerResolver;
        this.clock = clock;
    }

    public ServiceResult execute(CreateServiceCommand command) {
        Provider provider = providerResolver.requireActiveProvider(command.userId());
        ServiceOffering service = ServiceOffering.create(provider.getId(), command.name(), command.description(),
                command.price(), command.duration(), clock.instant());
        return ServiceResult.from(serviceRepository.save(service));
    }
}