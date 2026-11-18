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
import com.humanex.ticketdesk.exception.TicketOperationForbiddenException;
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

    public List<Ticket> findAllVisibleTo(
            Long userId,
            UserRole role) {

        if (role == UserRole.USER) {
            return ticketRepository.findByRequester_Id(userId);
        }

        return ticketRepository.findAll();
    }

    public Ticket findById(Long id) {

        return ticketRepository.findById(id)
                .orElseThrow(() ->
                        new TicketNotFoundException(id)
                );
    }

    public Ticket findByIdVisibleTo(
            Long ticketId,
            Long userId,
            UserRole role) {

        if (role == UserRole.USER) {
            return ticketRepository
                    .findByIdAndRequester_Id(
                            ticketId,
                            userId
                    )
                    .orElseThrow(() ->
                            new TicketNotFoundException(ticketId)
                    );
        }

        return findById(ticketId);
    }

    public List<TicketStatusHistory> findHistoryByTicketId(
            Long ticketId) {

        findById(ticketId);

        return ticketStatusHistoryRepository
                .findByTicket_IdOrderByChangedAtAsc(ticketId);
    }

    public List<TicketStatusHistory> findHistoryVisibleTo(
            Long ticketId,
            Long userId,
            UserRole role) {

        findByIdVisibleTo(
                ticketId,
                userId,
                role
        );

        return ticketStatusHistoryRepository
                .findByTicket_IdOrderByChangedAtAsc(ticketId);
    }

    @Transactional
    public Ticket create(
            String title,
            String description,
            TicketPriority priority,
            Long requesterId) {

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() ->
                        new UserNotFoundException(requesterId)
                );

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
    public Ticket assign(
            Long ticketId,
            Long assigneeId) {

        Ticket ticket = findById(ticketId);

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() ->
                        new UserNotFoundException(assigneeId)
                );

        validateAssignee(assignee);

        ticket.assignTo(assignee);

        return ticket;
    }

    @Transactional
    public Ticket assignAuthorized(
            Long ticketId,
            Long assigneeId,
            Long actorUserId,
            UserRole actorRole) {

        if (actorRole == UserRole.ADMIN) {
            return assign(ticketId, assigneeId);
        }

        if (actorRole != UserRole.TECHNICIAN) {
            throw new TicketOperationForbiddenException(
                    "No tienes permiso para asignar este ticket."
            );
        }

        Ticket ticket = findById(ticketId);

        if (!actorUserId.equals(assigneeId)) {
            throw new TicketOperationForbiddenException(
                    "Un técnico solo puede asignarse un ticket a sí mismo."
            );
        }

        if (ticket.getAssignee() != null
                && !ticket.getAssignee().getId().equals(actorUserId)) {

            throw new TicketOperationForbiddenException(
                    "El ticket ya está asignado a otro técnico."
            );
        }

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() ->
                        new UserNotFoundException(assigneeId)
                );

        validateAssignee(assignee);

        ticket.assignTo(assignee);

        return ticket;
    }

    @Transactional
    public Ticket updateStatus(
            Long id,
            TicketStatus newStatus) {

        Ticket ticket = findById(id);

        return updateStatusInternal(
                ticket,
                newStatus
        );
    }

    @Transactional
    public Ticket updateStatusAuthorized(
            Long ticketId,
            TicketStatus newStatus,
            Long actorUserId,
            UserRole actorRole) {

        Ticket ticket = findById(ticketId);

        if (actorRole == UserRole.TECHNICIAN) {

            if (ticket.getAssignee() == null
                    || !ticket.getAssignee()
                            .getId()
                            .equals(actorUserId)) {

                throw new TicketOperationForbiddenException(
                        "Solo puedes cambiar el estado de tickets asignados a ti."
                );
            }

        } else if (actorRole != UserRole.ADMIN) {

            throw new TicketOperationForbiddenException(
                    "No tienes permiso para cambiar el estado de este ticket."
            );
        }

        return updateStatusInternal(
                ticket,
                newStatus
        );
    }

    private void validateAssignee(User assignee) {

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
    }

    private Ticket updateStatusInternal(
            Ticket ticket,
            TicketStatus newStatus) {

        TicketStatus currentStatus = ticket.getStatus();

        if (!isValidStatusTransition(
                currentStatus,
                newStatus)) {

            throw new InvalidTicketStatusTransitionException(
                    ticket.getId(),
                    currentStatus,
                    newStatus
            );
        }

        TicketStatusHistory history =
                new TicketStatusHistory(
                        ticket,
                        currentStatus,
                        newStatus
                );

        ticket.setStatus(newStatus);

        ticketStatusHistoryRepository.save(history);

        return ticket;
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