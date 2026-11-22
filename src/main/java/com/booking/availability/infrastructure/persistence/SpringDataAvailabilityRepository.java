package com.booking.availability.infrastructure.persistence;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataAvailabilityRepository extends JpaRepository<AvailabilityJpaEntity, Long> {

    List<AvailabilityJpaEntity> findByProviderIdOrderByStartTimeAsc(Long providerId);

    boolean existsByProviderIdAndStartTimeLessThanAndEndTimeGreaterThan(Long providerId, Instant end, Instant start);
}