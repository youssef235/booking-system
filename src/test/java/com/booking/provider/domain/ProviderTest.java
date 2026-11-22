package com.booking.provider.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class ProviderTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant LATER = Instant.parse("2026-01-02T10:00:00Z");

    @Test
    void registerCreatesActiveProviderAndTrimsData() {
        Provider provider = Provider.register(7L, "  Clinic A ", "  Dental care ", NOW);

        assertEquals("Clinic A", provider.getName());
        assertEquals("Dental care", provider.getDescription());
        assertEquals(ProviderStatus.ACTIVE, provider.getStatus());
        assertEquals(7L, provider.getUserId());
        assertNull(provider.getId());
        assertTrue(provider.acceptsBookings());
    }

    @Test
    void blankDescriptionIsStoredAsNull() {
        Provider provider = Provider.register(7L, "Clinic A", "   ", NOW);

        assertNull(provider.getDescription());
    }

    @Test
    void registerWithoutUserFails() {
        assertThrows(InvalidProviderException.class, () -> Provider.register(null, "Clinic A", null, NOW));
    }

    @Test
    void registerWithBlankNameFails() {
        assertThrows(InvalidProviderException.class, () -> Provider.register(7L, " ", null, NOW));
    }

    @Test
    void suspendedProviderDoesNotAcceptBookings() {
        Provider provider = Provider.register(7L, "Clinic A", null, NOW);

        provider.suspend(LATER);

        assertEquals(ProviderStatus.SUSPENDED, provider.getStatus());
        assertFalse(provider.acceptsBookings());
        assertEquals(LATER, provider.getUpdatedAt());
    }

    @Test
    void updateDetailsChangesNameAndTimestamp() {
        Provider provider = Provider.register(7L, "Clinic A", null, NOW);

        provider.updateDetails("Clinic B", "New description", LATER);

        assertEquals("Clinic B", provider.getName());
        assertEquals("New description", provider.getDescription());
        assertEquals(LATER, provider.getUpdatedAt());
        assertEquals(NOW, provider.getCreatedAt());
    }
}
