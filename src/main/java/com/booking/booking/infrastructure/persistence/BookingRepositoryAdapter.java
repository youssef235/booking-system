package com.booking.booking.infrastructure.persistence;

import com.booking.booking.domain.Booking;
import com.booking.booking.domain.BookingRepository;
import com.booking.booking.domain.BookingStatus;
import com.booking.shared.domain.ConflictException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
class BookingRepositoryAdapter implements BookingRepository {

    private static final String OVERLAP_CONSTRAINT = "ex_bookings_no_overlap";

    private static final List<BookingStatus> SLOT_OCCUPYING = Arrays.stream(BookingStatus.values())
            .filter(BookingStatus::occupiesSlot)
            .toList();

    private final SpringDataBookingRepository springDataRepository;

    BookingRepositoryAdapter(SpringDataBookingRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Booking save(Booking booking) {
        try {
            return BookingMapper.toDomain(springDataRepository.save(BookingMapper.toEntity(booking)));
        } catch (DataIntegrityViolationException ex) {
            if (isOverlapViolation(ex)) {
                throw new ConflictException("BOOKING_SLOT_TAKEN", "This time slot is already booked");
            }
            throw ex;
        }
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return springDataRepository.findById(id).map(BookingMapper::toDomain);
    }

    @Override
    public List<Booking> findByUserId(Long userId) {
        return springDataRepository.findByUserIdOrderByStartTimeDesc(userId).stream()
                .map(BookingMapper::toDomain)
                .toList();
    }

    @Override
    public List<Booking> findByProviderId(Long providerId) {
        return springDataRepository.findByProviderIdOrderByStartTimeDesc(providerId).stream()
                .map(BookingMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsOverlapping(Long providerId, Instant start, Instant end) {
        return springDataRepository.existsByProviderIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                providerId, SLOT_OCCUPYING, end, start);
    }

    private static boolean isOverlapViolation(DataIntegrityViolationException ex) {
        String message = NestedExceptionUtils.getMostSpecificCause(ex).getMessage();
        return message != null && message.contains(OVERLAP_CONSTRAINT);
    }
}