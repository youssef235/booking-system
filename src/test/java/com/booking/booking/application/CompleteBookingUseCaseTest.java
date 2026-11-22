package com.booking.booking.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.booking.booking.domain.Booking;
import com.booking.booking.domain.BookingRepository;
import com.booking.booking.domain.BookingStatus;
import com.booking.booking.domain.InvalidBookingException;
import com.booking.booking.domain.InvalidBookingTransitionException;
import com.booking.provider.application.ProviderResolver;
import com.booking.provider.domain.Provider;
import com.booking.provider.domain.ProviderRepository;
import com.booking.provider.domain.ProviderStatus;
import com.booking.shared.domain.NotFoundException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CompleteBookingUseCaseTest {

    private static final Instant CREATED = Instant.parse("2029-12-01T00:00:00Z");
    private static final Instant START = Instant.parse("2030-01-01T10:00:00Z");
    private static final Instant END = Instant.parse("2030-01-01T11:00:00Z");
    private static final Long OWNER_USER = 200L;
    private static final Long PROVIDER_USER = 100L;
    private static final Long OTHER_PROVIDER_USER = 101L;

    @Test
    void completesConfirmedBookingAfterItEnds() {
        FakeBookings bookings = new FakeBookings(booking(BookingStatus.CONFIRMED));
        CompleteBookingUseCase useCase = useCase(bookings, END.plusSeconds(1));

        BookingResult result = useCase.execute(PROVIDER_USER, 1L);

        assertEquals(BookingStatus.COMPLETED, result.status());
        assertEquals(BookingStatus.COMPLETED, bookings.findById(1L).orElseThrow().getStatus());
    }

    @Test
    void rejectsCompletionBeforeTheBookingEnds() {
        CompleteBookingUseCase useCase = useCase(new FakeBookings(booking(BookingStatus.CONFIRMED)),
                END.minusSeconds(1));

        assertThrows(InvalidBookingException.class, () -> useCase.execute(PROVIDER_USER, 1L));
    }

    @Test
    void rejectsCompletionOfPendingBooking() {
        CompleteBookingUseCase useCase = useCase(new FakeBookings(booking(BookingStatus.PENDING)),
                END.plusSeconds(1));

        assertThrows(InvalidBookingTransitionException.class, () -> useCase.execute(PROVIDER_USER, 1L));
    }

    @Test
    void hidesBookingOfAnotherProvider() {
        CompleteBookingUseCase useCase = useCase(new FakeBookings(booking(BookingStatus.CONFIRMED)),
                END.plusSeconds(1));

        assertThrows(NotFoundException.class, () -> useCase.execute(OTHER_PROVIDER_USER, 1L));
    }

    private static Booking booking(BookingStatus status) {
        return Booking.restore(1L, OWNER_USER, 300L, 5L, START, END, status, CREATED, CREATED);
    }

    private static CompleteBookingUseCase useCase(FakeBookings bookings, Instant now) {
        ProviderResolver resolver = new ProviderResolver(new FakeProviders());
        return new CompleteBookingUseCase(bookings, resolver, Clock.fixed(now, ZoneOffset.UTC));
    }

    private static final class FakeBookings implements BookingRepository {

        private final Map<Long, Booking> store = new HashMap<>();

        FakeBookings(Booking booking) {
            store.put(booking.getId(), booking);
        }

        @Override
        public Booking save(Booking booking) {
            store.put(booking.getId(), booking);
            return booking;
        }

        @Override
        public Optional<Booking> findById(Long id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<Booking> findByUserId(Long userId) {
            return new ArrayList<>(store.values());
        }

        @Override
        public List<Booking> findByProviderId(Long providerId) {
            return new ArrayList<>(store.values());
        }

        @Override
        public boolean existsOverlapping(Long providerId, Instant start, Instant end) {
            return false;
        }
    }

    private static final class FakeProviders implements ProviderRepository {

        private final Provider first = Provider.restore(5L, PROVIDER_USER, "First", null,
                ProviderStatus.ACTIVE, CREATED, CREATED);
        private final Provider second = Provider.restore(6L, OTHER_PROVIDER_USER, "Second", null,
                ProviderStatus.ACTIVE, CREATED, CREATED);

        @Override
        public Provider save(Provider provider) {
            return provider;
        }

        @Override
        public Optional<Provider> findById(Long id) {
            return List.of(first, second).stream().filter(p -> p.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<Provider> findByUserId(Long userId) {
            return List.of(first, second).stream().filter(p -> p.getUserId().equals(userId)).findFirst();
        }

        @Override
        public boolean existsByUserId(Long userId) {
            return findByUserId(userId).isPresent();
        }

        @Override
        public List<Provider> findAllAcceptingBookings() {
            return List.of(first, second);
        }
    }
}