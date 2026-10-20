package com.humanex.ticketdesk.dto;

import java.time.LocalDateTime;

import com.humanex.ticketdesk.enums.UserRole;

public record UserResponse(
        Long id,
        String name,
        String email,
        UserRole role,
        boolean enabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}