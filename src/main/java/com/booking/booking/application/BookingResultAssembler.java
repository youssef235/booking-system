package com.booking.booking.application;

import com.booking.booking.domain.Booking;
import com.booking.provider.domain.Provider;
import com.booking.provider.domain.ProviderRepository;
import com.booking.service.domain.ServiceOffering;
import com.booking.service.domain.ServiceOfferingRepository;
import com.booking.user.domain.User;
import com.booking.user.domain.UserRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class BookingResultAssembler {

    private final ServiceOfferingRepository serviceRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;

    public BookingResultAssembler(ServiceOfferingRepository serviceRepository, ProviderRepository providerRepository,
                                  UserRepository userRepository) {
        this.serviceRepository = serviceRepository;
        this.providerRepository = providerRepository;
        this.userRepository = userRepository;
    }

    public List<BookingResult> assemble(List<Booking> bookings) {
        return build(bookings, false);
    }

    public List<BookingResult> assembleWithCustomers(List<Booking> bookings) {
        return build(bookings, true);
    }

    private List<BookingResult> build(List<Booking> bookings, boolean withCustomers) {
        Map<Long, String> serviceNames = new HashMap<>();
        Map<Long, String> providerNames = new HashMap<>();
        Map<Long, String> customerNames = new HashMap<>();
        return bookings.stream().map(booking -> BookingResult.from(booking,
                cached(serviceNames, booking.getServiceId(),
                        id -> serviceRepository.findById(id).map(ServiceOffering::getName)),
                cached(providerNames, booking.getProviderId(),
                        id -> providerRepository.findById(id).map(Provider::getName)),
                withCustomers
                        ? cached(customerNames, booking.getUserId(), id -> userRepository.findById(id).map(User::getName))
                        : null)).toList();
    }

    private static String cached(Map<Long, String> cache, Long id, Function<Long, Optional<String>> loader) {
        if (!cache.containsKey(id)) {
            cache.put(id, loader.apply(id).orElse(null));
        }
        return cache.get(id);
    }
}