package com.humanex.ticketdesk.exception;

import com.humanex.ticketdesk.enums.UserRole;

public class InvalidAssigneeRoleException extends RuntimeException {

    public InvalidAssigneeRoleException(Long userId, UserRole role) {
        super(
                "El usuario con id " + userId
                + " no puede ser asignado a un ticket porque su rol es "
                + role
                + ". Se requiere el rol TECHNICIAN."
        );
    }
}