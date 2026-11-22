package com.booking.service.infrastructure.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataServiceOfferingRepository extends JpaRepository<ServiceOfferingJpaEntity, Long> {

    List<ServiceOfferingJpaEntity> findByProviderId(Long providerId);
}