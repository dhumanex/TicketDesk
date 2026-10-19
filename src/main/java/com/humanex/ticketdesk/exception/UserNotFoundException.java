package com.humanex.ticketdesk.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("No existe ningún usuario con id " + id);
    }

    public UserNotFoundException(String email) {
        super("No existe ningún usuario con email " + email);
    }
}