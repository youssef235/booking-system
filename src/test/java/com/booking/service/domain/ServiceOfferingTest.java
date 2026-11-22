package com.booking.service.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ServiceOfferingTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant LATER = Instant.parse("2026-01-02T10:00:00Z");

    @Test
    void createBuildsActiveServiceWithNormalizedValues() {
        ServiceOffering service = ServiceOffering.create(3L, "  Checkup ", "  Full checkup ",
                new BigDecimal("150"), Duration.ofMinutes(30), NOW);

        assertEquals("Checkup", service.getName());
        assertEquals("Full checkup", service.getDescription());
        assertEquals(new BigDecimal("150.00"), service.getPrice());
        assertEquals(Duration.ofMinutes(30), service.getDuration());
        assertTrue(service.isActive());
        assertNull(service.getId());
        assertTrue(service.belongsTo(3L));
        assertFalse(service.belongsTo(4L));
    }

    @Test
    void createWithoutProviderFails() {
        assertThrows(InvalidServiceException.class,
                () -> ServiceOffering.create(null, "Checkup", null, BigDecimal.TEN, Duration.ofMinutes(30), NOW));
    }

    @Test
    void createWithNegativePriceFails() {
        assertThrows(InvalidServiceException.class,
                () -> ServiceOffering.create(3L, "Checkup", null, new BigDecimal("-1"), Duration.ofMinutes(30), NOW));
    }

    @Test
    void createWithTooManyDecimalPlacesFails() {
        assertThrows(InvalidServiceException.class,
                () -> ServiceOffering.create(3L, "Checkup", null, new BigDecimal("10.999"), Duration.ofMinutes(30), NOW));
    }

    @Test
    void createWithZeroDurationFails() {
        assertThrows(InvalidServiceException.class,
                () -> ServiceOffering.create(3L, "Checkup", null, BigDecimal.TEN, Duration.ZERO, NOW));
    }

    @Test
    void createWithDurationOverLimitFails() {
        assertThrows(InvalidServiceException.class,
                () -> ServiceOffering.create(3L, "Checkup", null, BigDecimal.TEN, Duration.ofHours(9), NOW));
    }

    @Test
    void updateChangesValuesAndTimestamp() {
        ServiceOffering service = ServiceOffering.create(3L, "Checkup", null, BigDecimal.TEN,
                Duration.ofMinutes(30), NOW);

        service.update("Deep checkup", "Detailed", new BigDecimal("99.50"), Duration.ofMinutes(45), LATER);

        assertEquals("Deep checkup", service.getName());
        assertEquals(new BigDecimal("99.50"), service.getPrice());
        assertEquals(Duration.ofMinutes(45), service.getDuration());
        assertEquals(LATER, service.getUpdatedAt());
    }

    @Test
    void deactivateTurnsServiceOff() {
        ServiceOffering service = ServiceOffering.create(3L, "Checkup", null, BigDecimal.TEN,
                Duration.ofMinutes(30), NOW);

        service.deactivate(LATER);

        assertFalse(service.isActive());
    }
}
