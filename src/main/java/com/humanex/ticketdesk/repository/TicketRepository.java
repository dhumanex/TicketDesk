package com.humanex.ticketdesk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.humanex.ticketdesk.entity.Ticket;

public interface TicketRepository extends
        JpaRepository<Ticket, Long>,
        JpaSpecificationExecutor<Ticket> {

    @Override
    @EntityGraph(attributePaths = {"requester", "assignee"})
    List<Ticket> findAll();

    @Override
    @EntityGraph(attributePaths = {"requester", "assignee"})
    Page<Ticket> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"requester", "assignee"})
    Optional<Ticket> findById(Long id);

    @EntityGraph(attributePaths = {"requester", "assignee"})
    List<Ticket> findByRequester_Id(Long requesterId);

    @EntityGraph(attributePaths = {"requester", "assignee"})
    Page<Ticket> findByRequester_Id(
            Long requesterId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"requester", "assignee"})
    Optional<Ticket> findByIdAndRequester_Id(
            Long ticketId,
            Long requesterId
    );

    @Override
    @EntityGraph(attributePaths = {"requester", "assignee"})
    Page<Ticket> findAll(
            Specification<Ticket> specification,
            Pageable pageable
    );
}