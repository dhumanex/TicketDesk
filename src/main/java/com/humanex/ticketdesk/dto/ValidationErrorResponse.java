package com.humanex.ticketdesk.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp,
        String path,
        Map<String, String> fields
) {
}