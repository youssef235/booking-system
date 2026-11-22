package com.booking.booking.application;

import com.booking.availability.domain.Availability;
import com.booking.availability.domain.AvailabilityRepository;
import com.booking.availability.domain.TimeSlot;
import com.booking.booking.domain.Booking;
import com.booking.booking.domain.BookingRepository;
import com.booking.provider.domain.Provider;
import com.booking.provider.domain.ProviderRepository;
import com.booking.service.domain.ServiceOffering;
import com.booking.service.domain.ServiceOfferingRepository;
import com.booking.shared.domain.NotFoundException;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public class ListAvailableSlotsUseCase {

    private final ServiceOfferingRepository serviceRepository;
    private final ProviderRepository providerRepository;
    private final AvailabilityRepository availabilityRepository;
    private final BookingRepository bookingRepository;
    private final Clock clock;

    public ListAvailableSlotsUseCase(ServiceOfferingRepository serviceRepository,
                                     ProviderRepository providerRepository,
                                     AvailabilityRepository availabilityRepository,
                                     BookingRepository bookingRepository, Clock clock) {
        this.serviceRepository = serviceRepository;
        this.providerRepository = providerRepository;
        this.availabilityRepository = availabilityRepository;
        this.bookingRepository = bookingRepository;
        this.clock = clock;
    }

    public List<SlotResult> execute(Long serviceId) {
        ServiceOffering service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));
        if (!service.isActive()) {
            return List.of();
        }
        Provider provider = providerRepository.findById(service.getProviderId())
                .orElseThrow(() -> new NotFoundException("Provider not found"));
        if (!provider.acceptsBookings()) {
            return List.of();
        }
        Instant now = clock.instant();
        List<Booking> occupied = bookingRepository.findByProviderId(provider.getId()).stream()
                .filter(booking -> booking.getStatus().occupiesSlot())
                .toList();
        return availabilityRepository.findByProviderId(provider.getId()).stream()
                .filter(Availability::isOpen)
                .filter(availability -> availability.getEndTime().isAfter(now))
                .flatMap(availability -> availability.slots(service.getDuration()).stream())
                .filter(slot -> slot.start().isAfter(now))
                .filter(slot -> occupied.stream().noneMatch(booking -> booking.overlaps(slot.start(), slot.end())))
                .distinct()
                .sorted(Comparator.comparing(TimeSlot::start))
                .map(slot -> new SlotResult(slot.start(), slot.end()))
                .toList();
    }
}