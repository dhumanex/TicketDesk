package com.humanex.ticketdesk.dto;

public record LoginResponse(

        String accessToken,
        String tokenType,
        long expiresIn

) {
}