package com.booking.booking.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.booking.availability.domain.Availability;
import com.booking.availability.domain.AvailabilityRepository;
import com.booking.provider.domain.Provider;
import com.booking.provider.domain.ProviderRepository;
import com.booking.service.domain.ServiceOffering;
import com.booking.service.domain.ServiceOfferingRepository;
import com.booking.shared.domain.ConflictException;
import com.booking.user.domain.User;
import com.booking.user.domain.UserRepository;
import com.booking.user.domain.UserRole;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class CreateBookingConcurrencyIntegrationTest {

    private static final int ATTEMPTS = 8;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private ServiceOfferingRepository serviceOfferingRepository;
    @Autowired
    private AvailabilityRepository availabilityRepository;
    @Autowired
    private CreateBookingUseCase createBookingUseCase;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final List<Long> userIds = new ArrayList<>();
    private Long providerId;

    @AfterEach
    void cleanUp() {
        if (providerId != null) {
            jdbcTemplate.update("DELETE FROM bookings WHERE provider_id = ?", providerId);
            jdbcTemplate.update("DELETE FROM availabilities WHERE provider_id = ?", providerId);
            jdbcTemplate.update("DELETE FROM services WHERE provider_id = ?", providerId);
            jdbcTemplate.update("DELETE FROM providers WHERE id = ?", providerId);
        }
        for (Long id : userIds) {
            jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
        }
    }

    @Test
    void onlyOneOfManyParallelBookingsForTheSameSlotSucceeds() throws Exception {
        Instant now = Instant.now();
        String tag = UUID.randomUUID().toString().substring(0, 8);

        User owner = saveUser("owner", tag, now);
        List<Long> bookers = new ArrayList<>();
        for (int i = 0; i < ATTEMPTS; i++) {
            bookers.add(saveUser("booker" + i, tag, now).getId());
        }
        Provider provider = providerRepository.save(Provider.register(owner.getId(), "Concurrency Salon", null, now));
        providerId = provider.getId();
        ServiceOffering service = serviceOfferingRepository.save(
                ServiceOffering.create(providerId, "Cut", null, BigDecimal.TEN, Duration.ofMinutes(60), now));
        Instant start = now.plus(Duration.ofDays(2));
        availabilityRepository.save(Availability.create(providerId, start.minus(Duration.ofHours(1)),
                start.plus(Duration.ofHours(3)), now));

        ExecutorService pool = Executors.newFixedThreadPool(ATTEMPTS);
        try {
            CountDownLatch ready = new CountDownLatch(ATTEMPTS);
            CountDownLatch go = new CountDownLatch(1);
            List<Future<BookingResult>> futures = new ArrayList<>();
            for (Long bookerId : bookers) {
                futures.add(pool.submit(() -> {
                    ready.countDown();
                    go.await();
                    return createBookingUseCase.execute(new CreateBookingCommand(bookerId, service.getId(), start));
                }));
            }
            ready.await();
            go.countDown();

            int successes = 0;
            int conflicts = 0;
            List<String> unexpected = new ArrayList<>();
            for (Future<BookingResult> future : futures) {
                try {
                    future.get();
                    successes++;
                } catch (ExecutionException ex) {
                    if (ex.getCause() instanceof ConflictException conflict
                            && "BOOKING_SLOT_TAKEN".equals(conflict.getCode())) {
                        conflicts++;
                    } else {
                        unexpected.add(String.valueOf(ex.getCause()));
                    }
                }
            }

            assertEquals(1, successes, "exactly one booking must succeed");
            assertEquals(ATTEMPTS - 1, conflicts, "all others must get BOOKING_SLOT_TAKEN, unexpected: " + unexpected);
            Integer stored = jdbcTemplate.queryForObject(
                    "SELECT count(*) FROM bookings WHERE provider_id = ?", Integer.class, providerId);
            assertEquals(1, stored);
        } finally {
            pool.shutdownNow();
        }
    }

    private User saveUser(String label, String tag, Instant now) {
        User saved = userRepository.save(
                User.register("Test " + label, label + "-" + tag + "@example.com", "x", UserRole.USER, now));
        userIds.add(saved.getId());
        return saved;
    }
}