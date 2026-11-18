package com.humanex.ticketdesk.exception;

public class TicketOperationForbiddenException extends RuntimeException {

    public TicketOperationForbiddenException(String message) {
        super(message);
    }
}