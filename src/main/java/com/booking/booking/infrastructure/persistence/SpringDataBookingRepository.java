package com.booking.booking.infrastructure.persistence;

import com.booking.booking.domain.BookingStatus;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataBookingRepository extends JpaRepository<BookingJpaEntity, Long> {

    List<BookingJpaEntity> findByUserIdOrderByStartTimeDesc(Long userId);

    List<BookingJpaEntity> findByProviderIdOrderByStartTimeDesc(Long providerId);

    boolean existsByProviderIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
            Long providerId, Collection<BookingStatus> statuses, Instant end, Instant start);
}