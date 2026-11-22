package com.booking.booking.application;

import com.booking.booking.domain.Booking;
import com.booking.booking.domain.BookingRepository;
import com.booking.provider.domain.ProviderRepository;
import com.booking.shared.domain.NotFoundException;
import java.time.Clock;

public class CancelBookingUseCase {

    private final BookingRepository bookingRepository;
    private final ProviderRepository providerRepository;
    private final Clock clock;

    public CancelBookingUseCase(BookingRepository bookingRepository, ProviderRepository providerRepository,
                                Clock clock) {
        this.bookingRepository = bookingRepository;
        this.providerRepository = providerRepository;
        this.clock = clock;
    }

    public BookingResult execute(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .filter(found -> canCancel(found, userId))
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        booking.cancel(clock.instant());
        return BookingResult.from(bookingRepository.save(booking));
    }

    private boolean canCancel(Booking booking, Long userId) {
        if (booking.isOwnedBy(userId)) {
            return true;
        }
        return providerRepository.findByUserId(userId)
                .map(provider -> booking.belongsToProvider(provider.getId()))
                .orElse(false);
    }
}