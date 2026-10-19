package com.humanex.ticketdesk.exception;

import com.humanex.ticketdesk.enums.TicketStatus;

public class InvalidTicketStatusTransitionException extends RuntimeException {

    public InvalidTicketStatusTransitionException(
            Long id,
            TicketStatus currentStatus,
            TicketStatus newStatus) {

        super(
                "No se puede cambiar el ticket " + id
                + " de " + currentStatus
                + " a " + newStatus
        );
    }
}