package com.booking.provider.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataProviderRepository extends JpaRepository<ProviderJpaEntity, Long> {

    Optional<ProviderJpaEntity> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}