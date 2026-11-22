package com.booking.provider.infrastructure.persistence;

import com.booking.provider.domain.Provider;
import com.booking.provider.domain.ProviderRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
class ProviderRepositoryAdapter implements ProviderRepository {

    private final SpringDataProviderRepository springDataProviderRepository;

    ProviderRepositoryAdapter(SpringDataProviderRepository springDataProviderRepository) {
        this.springDataProviderRepository = springDataProviderRepository;
    }

    @Override
    public Provider save(Provider provider) {
        return ProviderMapper.toDomain(springDataProviderRepository.save(ProviderMapper.toEntity(provider)));
    }

    @Override
    public Optional<Provider> findById(Long id) {
        return springDataProviderRepository.findById(id).map(ProviderMapper::toDomain);
    }

    @Override
    public Optional<Provider> findByUserId(Long userId) {
        return springDataProviderRepository.findByUserId(userId).map(ProviderMapper::toDomain);
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return springDataProviderRepository.existsByUserId(userId);
    }

    @Override
    public List<Provider> findAllAcceptingBookings() {
        return springDataProviderRepository.findAll(Sort.by("id")).stream()
                .map(ProviderMapper::toDomain)
                .filter(Provider::acceptsBookings)
                .toList();
    }
}