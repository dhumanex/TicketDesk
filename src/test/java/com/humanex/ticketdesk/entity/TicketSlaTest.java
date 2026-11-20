package com.humanex.ticketdesk.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.humanex.ticketdesk.enums.TicketPriority;
import com.humanex.ticketdesk.enums.TicketStatus;

class TicketSlaTest {

    @Test
    void shouldSetCriticalSlaToFourHours() {

        Ticket ticket = new Ticket(
                "Ticket crítico",
                "Descripción",
                TicketPriority.CRITICAL,
                null
        );

        ticket.onCreate();

        assertEquals(
                ticket.getCreatedAt().plusHours(4),
                ticket.getSlaDueAt()
        );
    }

    @Test
    void shouldSetHighSlaToEightHours() {

        Ticket ticket = new Ticket(
                "Ticket high",
                "Descripción",
                TicketPriority.HIGH,
                null
        );

        ticket.onCreate();

        assertEquals(
                ticket.getCreatedAt().plusHours(8),
                ticket.getSlaDueAt()
        );
    }

    @Test
    void shouldSetMediumSlaToTwentyFourHours() {

        Ticket ticket = new Ticket(
                "Ticket medium",
                "Descripción",
                TicketPriority.MEDIUM,
                null
        );

        ticket.onCreate();

        assertEquals(
                ticket.getCreatedAt().plusHours(24),
                ticket.getSlaDueAt()
        );
    }

    @Test
    void shouldSetLowSlaToFortyEightHours() {

        Ticket ticket = new Ticket(
                "Ticket low",
                "Descripción",
                TicketPriority.LOW,
                null
        );

        ticket.onCreate();

        assertEquals(
                ticket.getCreatedAt().plusHours(48),
                ticket.getSlaDueAt()
        );
    }

    @Test
    void shouldKeepOriginalSlaWhenTicketIsUpdated() {

        Ticket ticket = new Ticket(
                "Ticket",
                "Descripción",
                TicketPriority.HIGH,
                null
        );

        ticket.onCreate();

        LocalDateTime originalSlaDueAt =
                ticket.getSlaDueAt();

        ticket.setTitle("Título actualizado");
        ticket.onUpdate();

        assertEquals(
                originalSlaDueAt,
                ticket.getSlaDueAt()
        );
    }

    @Test
    void shouldNotBeBreachedBeforeSlaDueDate() {

        Ticket ticket = new Ticket(
                "Ticket",
                "Descripción",
                TicketPriority.CRITICAL,
                null
        );

        ticket.onCreate();

        LocalDateTime beforeDueDate =
                ticket.getSlaDueAt()
                        .minusMinutes(1);

        assertFalse(
                ticket.isSlaBreachedAt(
                        beforeDueDate
                )
        );
    }

    @Test
    void shouldBeBreachedAfterSlaDueDate() {

        Ticket ticket = new Ticket(
                "Ticket",
                "Descripción",
                TicketPriority.CRITICAL,
                null
        );

        ticket.onCreate();

        LocalDateTime afterDueDate =
                ticket.getSlaDueAt()
                        .plusMinutes(1);

        assertTrue(
                ticket.isSlaBreachedAt(
                        afterDueDate
                )
        );
    }

    @Test
    void resolvedTicketShouldNotBeBreached() {

        Ticket ticket = new Ticket(
                "Ticket",
                "Descripción",
                TicketPriority.CRITICAL,
                null
        );

        ticket.onCreate();
        ticket.setStatus(
                TicketStatus.RESOLVED
        );

        LocalDateTime afterDueDate =
                ticket.getSlaDueAt()
                        .plusHours(1);

        assertFalse(
                ticket.isSlaBreachedAt(
                        afterDueDate
                )
        );
    }

    @Test
    void closedTicketShouldNotBeBreached() {

        Ticket ticket = new Ticket(
                "Ticket",
                "Descripción",
                TicketPriority.CRITICAL,
                null
        );

        ticket.onCreate();
        ticket.setStatus(
                TicketStatus.CLOSED
        );

        LocalDateTime afterDueDate =
                ticket.getSlaDueAt()
                        .plusHours(1);

        assertFalse(
                ticket.isSlaBreachedAt(
                        afterDueDate
                )
        );
    }

    @Test
    void legacyTicketWithoutSlaShouldNotBeBreached() {

        Ticket legacyTicket =
                new Ticket();

        LocalDateTime currentTime =
                LocalDateTime.now()
                        .plusYears(10);

        assertFalse(
                legacyTicket.isSlaBreachedAt(
                        currentTime
                )
        );
    }
}