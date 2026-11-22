package com.booking.booking.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class BookingTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant START = Instant.parse("2026-01-02T12:00:00Z");
    private static final Instant END = Instant.parse("2026-01-02T12:30:00Z");
    private static final Instant AFTER_START = Instant.parse("2026-01-02T12:10:00Z");
    private static final Instant AFTER_END = Instant.parse("2026-01-02T13:00:00Z");

    private Booking pendingBooking() {
        return Booking.create(1L, 2L, 3L, START, END, NOW);
    }

    @Test
    void createBuildsPendingBooking() {
        Booking booking = pendingBooking();

        assertEquals(BookingStatus.PENDING, booking.getStatus());
        assertNull(booking.getId());
        assertEquals(START, booking.getStartTime());
        assertEquals(NOW, booking.getCreatedAt());
    }

    @Test
    void createWithStartInPastFails() {
        assertThrows(InvalidBookingException.class,
                () -> Booking.create(1L, 2L, 3L, Instant.parse("2025-12-31T12:00:00Z"), END, NOW));
    }

    @Test
    void createWithEndBeforeStartFails() {
        assertThrows(InvalidBookingException.class, () -> Booking.create(1L, 2L, 3L, END, START, NOW));
    }

    @Test
    void createWithoutUserFails() {
        assertThrows(InvalidBookingException.class, () -> Booking.create(null, 2L, 3L, START, END, NOW));
    }

    @Test
    void confirmMovesPendingToConfirmed() {
        Booking booking = pendingBooking();

        booking.confirm(NOW);

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    }

    @Test
    void confirmingCancelledBookingFails() {
        Booking booking = pendingBooking();
        booking.cancel(NOW);

        assertThrows(InvalidBookingTransitionException.class, () -> booking.confirm(NOW));
    }

    @Test
    void rejectMovesPendingToRejected() {
        Booking booking = pendingBooking();

        booking.reject(NOW);

        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }

    @Test
    void cancelBeforeStartSucceeds() {
        Booking booking = pendingBooking();

        booking.cancel(NOW);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(NOW, booking.getUpdatedAt());
    }

    @Test
    void cancelAfterStartFails() {
        Booking booking = pendingBooking();

        assertThrows(InvalidBookingException.class, () -> booking.cancel(AFTER_START));
    }

    @Test
    void completeAfterEndSucceedsForConfirmedBooking() {
        Booking booking = pendingBooking();
        booking.confirm(NOW);

        booking.complete(AFTER_END);

        assertEquals(BookingStatus.COMPLETED, booking.getStatus());
    }

    @Test
    void completeBeforeEndFails() {
        Booking booking = pendingBooking();
        booking.confirm(NOW);

        assertThrows(InvalidBookingException.class, () -> booking.complete(AFTER_START));
    }

    @Test
    void completingPendingBookingFails() {
        Booking booking = pendingBooking();

        assertThrows(InvalidBookingTransitionException.class, () -> booking.complete(AFTER_END));
    }

    @Test
    void reschedulePendingBookingChangesTimes() {
        Booking booking = pendingBooking();
        Instant newStart = Instant.parse("2026-01-03T12:00:00Z");
        Instant newEnd = Instant.parse("2026-01-03T12:30:00Z");

        booking.reschedule(newStart, newEnd, NOW);

        assertEquals(newStart, booking.getStartTime());
        assertEquals(newEnd, booking.getEndTime());
    }

    @Test
    void rescheduleConfirmedBookingFails() {
        Booking booking = pendingBooking();
        booking.confirm(NOW);

        assertThrows(InvalidBookingException.class,
                () -> booking.reschedule(Instant.parse("2026-01-03T12:00:00Z"),
                        Instant.parse("2026-01-03T12:30:00Z"), NOW));
    }

    @Test
    void overlapsDetectsOverlapAndIgnoresTouchingEdges() {
        Booking booking = pendingBooking();

        assertTrue(booking.overlaps(AFTER_START, AFTER_END));
        assertFalse(booking.overlaps(END, AFTER_END));
        assertFalse(booking.overlaps(NOW, START));
    }

    @Test
    void ownershipChecksCompareIds() {
        Booking booking = pendingBooking();

        assertTrue(booking.isOwnedBy(1L));
        assertFalse(booking.isOwnedBy(9L));
        assertTrue(booking.belongsToProvider(3L));
        assertFalse(booking.belongsToProvider(9L));
    }
}
