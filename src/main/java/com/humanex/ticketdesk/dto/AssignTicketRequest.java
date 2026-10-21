package com.humanex.ticketdesk.dto;

import jakarta.validation.constraints.NotNull;

public record AssignTicketRequest(

        @NotNull(message = "El técnico es obligatorio")
        Long assigneeId
) {
}