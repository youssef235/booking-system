package com.booking.service.infrastructure.persistence;

import com.booking.service.domain.ServiceOffering;
import com.booking.service.domain.ServiceOfferingRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class ServiceOfferingRepositoryAdapter implements ServiceOfferingRepository {

    private final SpringDataServiceOfferingRepository springDataRepository;

    ServiceOfferingRepositoryAdapter(SpringDataServiceOfferingRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public ServiceOffering save(ServiceOffering service) {
        return ServiceOfferingMapper.toDomain(springDataRepository.save(ServiceOfferingMapper.toEntity(service)));
    }

    @Override
    public Optional<ServiceOffering> findById(Long id) {
        return springDataRepository.findById(id).map(ServiceOfferingMapper::toDomain);
    }

    @Override
    public List<ServiceOffering> findByProviderId(Long providerId) {
        return springDataRepository.findByProviderId(providerId).stream()
                .map(ServiceOfferingMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }
}