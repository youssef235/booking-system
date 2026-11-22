package com.booking.booking.application;

import com.booking.booking.domain.Booking;
import com.booking.booking.domain.BookingRepository;
import com.booking.provider.application.ProviderResolver;
import com.booking.provider.domain.Provider;
import com.booking.shared.domain.NotFoundException;
import java.time.Clock;

public class CompleteBookingUseCase {

    private final BookingRepository bookingRepository;
    private final ProviderResolver providerResolver;
    private final Clock clock;

    public CompleteBookingUseCase(BookingRepository bookingRepository, ProviderResolver providerResolver,
                                  Clock clock) {
        this.bookingRepository = bookingRepository;
        this.providerResolver = providerResolver;
        this.clock = clock;
    }

    public BookingResult execute(Long userId, Long bookingId) {
        Provider provider = providerResolver.requireProvider(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .filter(found -> found.belongsToProvider(provider.getId()))
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        booking.complete(clock.instant());
        return BookingResult.from(bookingRepository.save(booking));
    }
}