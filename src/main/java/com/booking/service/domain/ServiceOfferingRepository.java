package com.booking.service.domain;

import java.util.List;
import java.util.Optional;

public interface ServiceOfferingRepository {

    ServiceOffering save(ServiceOffering service);

    Optional<ServiceOffering> findById(Long id);

    List<ServiceOffering> findByProviderId(Long providerId);

    void deleteById(Long id);
}
