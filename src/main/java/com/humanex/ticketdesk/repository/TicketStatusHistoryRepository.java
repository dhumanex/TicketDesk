package com.humanex.ticketdesk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.humanex.ticketdesk.entity.TicketStatusHistory;

public interface TicketStatusHistoryRepository
        extends JpaRepository<TicketStatusHistory, Long> {

    List<TicketStatusHistory> findByTicket_IdOrderByChangedAtAsc(Long ticketId);
}