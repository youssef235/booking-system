package com.booking.booking.infrastructure.persistence;

import com.booking.booking.domain.Booking;

final class BookingMapper {

    private BookingMapper() {
    }

    static BookingJpaEntity toEntity(Booking booking) {
        return new BookingJpaEntity(booking.getId(), booking.getUserId(), booking.getServiceId(),
                booking.getProviderId(), booking.getStartTime(), booking.getEndTime(), booking.getStatus(),
                booking.getCreatedAt(), booking.getUpdatedAt());
    }

    static Booking toDomain(BookingJpaEntity entity) {
        return Booking.restore(entity.getId(), entity.getUserId(), entity.getServiceId(), entity.getProviderId(),
                entity.getStartTime(), entity.getEndTime(), entity.getStatus(), entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}