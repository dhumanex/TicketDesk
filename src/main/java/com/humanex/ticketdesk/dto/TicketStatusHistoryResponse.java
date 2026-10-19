package com.humanex.ticketdesk.dto;

import java.time.LocalDateTime;

import com.humanex.ticketdesk.enums.TicketStatus;

public record TicketStatusHistoryResponse(
        Long id,
        TicketStatus oldStatus,
        TicketStatus newStatus,
        LocalDateTime changedAt
) {
}