package com.humanex.ticketdesk.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanex.ticketdesk.entity.Ticket;
import com.humanex.ticketdesk.entity.TicketStatusHistory;
import com.humanex.ticketdesk.entity.User;
import com.humanex.ticketdesk.enums.TicketPriority;
import com.humanex.ticketdesk.enums.TicketStatus;
import com.humanex.ticketdesk.enums.UserRole;
import com.humanex.ticketdesk.exception.InvalidAssigneeRoleException;
import com.humanex.ticketdesk.exception.InvalidTicketStatusTransitionException;
import com.humanex.ticketdesk.exception.TicketNotFoundException;
import com.humanex.ticketdesk.exception.UserDisabledException;
import com.humanex.ticketdesk.exception.UserNotFoundException;
import com.humanex.ticketdesk.repository.TicketRepository;
import com.humanex.ticketdesk.repository.TicketStatusHistoryRepository;
import com.humanex.ticketdesk.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketStatusHistoryRepository ticketStatusHistoryRepository;
    private final UserRepository userRepository;

    public TicketService(
            TicketRepository ticketRepository,
            TicketStatusHistoryRepository ticketStatusHistoryRepository,
            UserRepository userRepository) {

        this.ticketRepository = ticketRepository;
        this.ticketStatusHistoryRepository = ticketStatusHistoryRepository;
        this.userRepository = userRepository;
    }

    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public Ticket findById(Long id) {

        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    public List<TicketStatusHistory> findHistoryByTicketId(Long ticketId) {

        if (!ticketRepository.existsById(ticketId)) {
            throw new TicketNotFoundException(ticketId);
        }

        return ticketStatusHistoryRepository
                .findByTicket_IdOrderByChangedAtAsc(ticketId);
    }

    @Transactional
    public Ticket create(
            String title,
            String description,
            TicketPriority priority) {

        Ticket ticket = new Ticket(
                title,
                description,
                priority
        );

        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket create(
            String title,
            String description,
            TicketPriority priority,
            Long requesterId) {

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException(requesterId));

        if (!requester.isEnabled()) {
            throw new UserDisabledException(
                    requester.getId()
            );
        }

        Ticket ticket = new Ticket(
                title,
                description,
                priority,
                requester
        );

        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket assign(Long ticketId, Long assigneeId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new UserNotFoundException(assigneeId));

        if (assignee.getRole() != UserRole.TECHNICIAN) {
            throw new InvalidAssigneeRoleException(
                    assignee.getId(),
                    assignee.getRole()
            );
        }

        if (!assignee.isEnabled()) {
            throw new UserDisabledException(
                    assignee.getId()
            );
        }

        ticket.assignTo(assignee);

        return ticket;
    }

    @Transactional
    public Ticket updateStatus(
            Long id,
            TicketStatus newStatus) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        TicketStatus currentStatus = ticket.getStatus();

        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new InvalidTicketStatusTransitionException(
                    id,
                    currentStatus,
                    newStatus
            );
        }

        TicketStatusHistory history = new TicketStatusHistory(
                ticket,
                currentStatus,
                newStatus
        );

        ticket.setStatus(newStatus);

        ticketStatusHistoryRepository.save(history);

        return ticket;
    }

    @Transactional
    public void deleteById(Long id) {
        ticketRepository.deleteById(id);
    }

    private boolean isValidStatusTransition(
            TicketStatus currentStatus,
            TicketStatus newStatus) {

        return switch (currentStatus) {
            case OPEN ->
                newStatus == TicketStatus.IN_PROGRESS;

            case IN_PROGRESS ->
                newStatus == TicketStatus.RESOLVED;

            case RESOLVED ->
                newStatus == TicketStatus.CLOSED;

            case CLOSED ->
                false;
        };
    }
}