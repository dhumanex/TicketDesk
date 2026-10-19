package com.humanex.ticketdesk.dto;

import com.humanex.ticketdesk.enums.TicketStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateTicketStatusRequest(

        @NotNull(message = "El estado es obligatorio")
        TicketStatus status

) {
}