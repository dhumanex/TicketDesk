package com.humanex.ticketdesk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.humanex.ticketdesk.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Override
    @EntityGraph(attributePaths = {"requester", "assignee"})
    List<Ticket> findAll();

    @Override
    @EntityGraph(attributePaths = {"requester", "assignee"})
    Optional<Ticket> findById(Long id);

    @EntityGraph(attributePaths = {"requester", "assignee"})
    List<Ticket> findByRequester_Id(Long requesterId);

    @EntityGraph(attributePaths = {"requester", "assignee"})
    Optional<Ticket> findByIdAndRequester_Id(
            Long ticketId,
            Long requesterId
    );
}