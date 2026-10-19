package com.humanex.ticketdesk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.humanex.ticketdesk.entity.Ticket;

public interface TicketRepository
        extends JpaRepository<Ticket, Long> {
}