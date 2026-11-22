package com.booking.booking.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BookingStatusTest {

    @Test
    void pendingCanMoveToConfirmedCancelledOrRejectedOnly() {
        assertTrue(BookingStatus.PENDING.canTransitionTo(BookingStatus.CONFIRMED));
        assertTrue(BookingStatus.PENDING.canTransitionTo(BookingStatus.CANCELLED));
        assertTrue(BookingStatus.PENDING.canTransitionTo(BookingStatus.REJECTED));
        assertFalse(BookingStatus.PENDING.canTransitionTo(BookingStatus.COMPLETED));
    }

    @Test
    void confirmedCanMoveToCancelledOrCompletedOnly() {
        assertTrue(BookingStatus.CONFIRMED.canTransitionTo(BookingStatus.CANCELLED));
        assertTrue(BookingStatus.CONFIRMED.canTransitionTo(BookingStatus.COMPLETED));
        assertFalse(BookingStatus.CONFIRMED.canTransitionTo(BookingStatus.REJECTED));
        assertFalse(BookingStatus.CONFIRMED.canTransitionTo(BookingStatus.PENDING));
    }

    @Test
    void finalStatusesCannotMoveAnywhere() {
        for (BookingStatus target : BookingStatus.values()) {
            assertFalse(BookingStatus.CANCELLED.canTransitionTo(target));
            assertFalse(BookingStatus.REJECTED.canTransitionTo(target));
            assertFalse(BookingStatus.COMPLETED.canTransitionTo(target));
        }
    }

    @Test
    void onlyPendingAndConfirmedOccupyTheSlot() {
        assertTrue(BookingStatus.PENDING.occupiesSlot());
        assertTrue(BookingStatus.CONFIRMED.occupiesSlot());
        assertFalse(BookingStatus.CANCELLED.occupiesSlot());
        assertFalse(BookingStatus.REJECTED.occupiesSlot());
        assertFalse(BookingStatus.COMPLETED.occupiesSlot());
    }
}
