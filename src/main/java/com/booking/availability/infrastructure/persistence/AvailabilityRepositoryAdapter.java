package com.booking.availability.infrastructure.persistence;

import com.booking.availability.domain.Availability;
import com.booking.availability.domain.AvailabilityRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class AvailabilityRepositoryAdapter implements AvailabilityRepository {

    private final SpringDataAvailabilityRepository springDataRepository;

    AvailabilityRepositoryAdapter(SpringDataAvailabilityRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Availability save(Availability availability) {
        return AvailabilityMapper.toDomain(springDataRepository.save(AvailabilityMapper.toEntity(availability)));
    }

    @Override
    public Optional<Availability> findById(Long id) {
        return springDataRepository.findById(id).map(AvailabilityMapper::toDomain);
    }

    @Override
    public List<Availability> findByProviderId(Long providerId) {
        return springDataRepository.findByProviderIdOrderByStartTimeAsc(providerId).stream()
                .map(AvailabilityMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsOverlapping(Long providerId, Instant start, Instant end) {
        return springDataRepository.existsByProviderIdAndStartTimeLessThanAndEndTimeGreaterThan(
                providerId, end, start);
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }
}