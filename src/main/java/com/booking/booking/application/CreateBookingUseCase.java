package com.booking.booking.application;

import com.booking.availability.domain.AvailabilityRepository;
import com.booking.availability.domain.TimeSlot;
import com.booking.booking.domain.Booking;
import com.booking.booking.domain.BookingRepository;
import com.booking.provider.domain.Provider;
import com.booking.provider.domain.ProviderRepository;
import com.booking.service.domain.ServiceOffering;
import com.booking.service.domain.ServiceOfferingRepository;
import com.booking.shared.application.TransactionRunner;
import com.booking.shared.domain.ConflictException;
import com.booking.shared.domain.ForbiddenOperationException;
import com.booking.shared.domain.NotFoundException;
import java.time.Clock;
import java.time.Instant;

public class CreateBookingUseCase {

    private final BookingRepository bookingRepository;
    private final ServiceOfferingRepository serviceRepository;
    private final ProviderRepository providerRepository;
    private final AvailabilityRepository availabilityRepository;
    private final TransactionRunner transactionRunner;
    private final Clock clock;

    public CreateBookingUseCase(BookingRepository bookingRepository, ServiceOfferingRepository serviceRepository,
                                ProviderRepository providerRepository,
                                AvailabilityRepository availabilityRepository,
                                TransactionRunner transactionRunner, Clock clock) {
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
        this.providerRepository = providerRepository;
        this.availabilityRepository = availabilityRepository;
        this.transactionRunner = transactionRunner;
        this.clock = clock;
    }

    public BookingResult execute(CreateBookingCommand command) {
        ServiceOffering service = serviceRepository.findById(command.serviceId())
                .orElseThrow(() -> new NotFoundException("Service not found"));
        if (!service.isActive()) {
            throw new ConflictException("SERVICE_INACTIVE", "Service is not available");
        }
        Provider provider = providerRepository.findById(service.getProviderId())
                .orElseThrow(() -> new NotFoundException("Provider not found"));
        if (!provider.acceptsBookings()) {
            throw new ConflictException("PROVIDER_NOT_ACCEPTING_BOOKINGS", "Provider is not accepting bookings");
        }
        if (provider.getUserId().equals(command.userId())) {
            throw new ForbiddenOperationException("You cannot book your own service");
        }

        Instant start = command.startTime();
        Instant end = start == null ? null : start.plus(service.getDuration());
        Booking booking = Booking.create(command.userId(), service.getId(), provider.getId(), start, end,
                clock.instant());

        TimeSlot slot = new TimeSlot(booking.getStartTime(), booking.getEndTime());
        boolean insideOpenWindow = availabilityRepository.findByProviderId(provider.getId()).stream()
                .anyMatch(availability -> availability.canHold(slot));
        if (!insideOpenWindow) {
            throw new ConflictException("SLOT_NOT_AVAILABLE", "Requested time is outside the provider's open hours");
        }

        return transactionRunner.run(() -> {
            if (bookingRepository.existsOverlapping(provider.getId(), booking.getStartTime(),
                    booking.getEndTime())) {
                throw new ConflictException("BOOKING_SLOT_TAKEN", "This time slot is already booked");
            }
            return BookingResult.from(bookingRepository.save(booking));
        });
    }
}