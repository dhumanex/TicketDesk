package com.humanex.ticketdesk.dto;

import java.time.LocalDateTime;

import com.humanex.ticketdesk.enums.TicketPriority;
import com.humanex.ticketdesk.enums.TicketStatus;

public record TicketResponse(
        Long id,
        String title,
        String description,
        TicketStatus status,
        TicketPriority priority,
        UserSummaryResponse requester,
        UserSummaryResponse assignee,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}