package com.humanex.ticketdesk.exception;

public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(Long id) {
        super("No existe ningún ticket con id " + id);
    }
}