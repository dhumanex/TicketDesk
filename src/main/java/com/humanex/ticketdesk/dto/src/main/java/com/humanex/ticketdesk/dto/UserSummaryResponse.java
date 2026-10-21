package com.humanex.ticketdesk.dto;

import com.humanex.ticketdesk.enums.UserRole;

public record UserSummaryResponse(
        Long id,
        String name,
        String email,
        UserRole role
) {
}