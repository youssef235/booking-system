package com.booking.booking.application;

import com.booking.booking.domain.BookingRepository;
import com.booking.provider.application.ProviderResolver;
import java.util.List;

public class ListProviderBookingsUseCase {

    private final BookingRepository bookingRepository;
    private final ProviderResolver providerResolver;
    private final BookingResultAssembler assembler;

    public ListProviderBookingsUseCase(BookingRepository bookingRepository, ProviderResolver providerResolver,
                                       BookingResultAssembler assembler) {
        this.bookingRepository = bookingRepository;
        this.providerResolver = providerResolver;
        this.assembler = assembler;
    }

    public List<BookingResult> execute(Long userId) {
        Long providerId = providerResolver.requireProvider(userId).getId();
        return assembler.assembleWithCustomers(bookingRepository.findByProviderId(providerId));
    }
}