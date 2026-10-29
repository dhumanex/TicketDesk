package com.humanex.ticketdesk.exception;

public class UserDisabledException extends RuntimeException {

    public UserDisabledException(Long userId) {
        super(
                "El usuario con id " + userId
                + " está desactivado y no puede ser asignado a un ticket."
        );
    }
}