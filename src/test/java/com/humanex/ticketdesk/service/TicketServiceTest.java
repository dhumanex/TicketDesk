package com.humanex.ticketdesk.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.humanex.ticketdesk.entity.Ticket;
import com.humanex.ticketdesk.entity.User;
import com.humanex.ticketdesk.enums.TicketPriority;
import com.humanex.ticketdesk.enums.UserRole;
import com.humanex.ticketdesk.exception.InvalidAssigneeRoleException;
import com.humanex.ticketdesk.exception.UserDisabledException;
import com.humanex.ticketdesk.repository.TicketRepository;
import com.humanex.ticketdesk.repository.TicketStatusHistoryRepository;
import com.humanex.ticketdesk.repository.UserRepository;

class TicketServiceTest {

    private TicketRepository ticketRepository;
    private TicketStatusHistoryRepository ticketStatusHistoryRepository;
    private UserRepository userRepository;

    private TicketService ticketService;

    @BeforeEach
    void setUp() {

        ticketRepository = mock(TicketRepository.class);

        ticketStatusHistoryRepository =
                mock(TicketStatusHistoryRepository.class);

        userRepository = mock(UserRepository.class);

        ticketService = new TicketService(
                ticketRepository,
                ticketStatusHistoryRepository,
                userRepository
        );
    }

    @Test
    void shouldRejectAssignmentWhenUserIsNotTechnician() {

        Long ticketId = 4L;
        Long userId = 1L;

        Ticket ticket = mock(Ticket.class);
        User user = mock(User.class);

        when(ticketRepository.findById(ticketId))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(user.getId())
                .thenReturn(userId);

        when(user.getRole())
                .thenReturn(UserRole.USER);

        assertThrows(
                InvalidAssigneeRoleException.class,
                () -> ticketService.assign(ticketId, userId)
        );
    }

    @Test
    void shouldRejectAssignmentWhenTechnicianIsDisabled() {

        Long ticketId = 4L;
        Long technicianId = 2L;

        Ticket ticket = mock(Ticket.class);
        User technician = mock(User.class);

        when(ticketRepository.findById(ticketId))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        when(technician.getId())
                .thenReturn(technicianId);

        when(technician.getRole())
                .thenReturn(UserRole.TECHNICIAN);

        when(technician.isEnabled())
                .thenReturn(false);

        assertThrows(
                UserDisabledException.class,
                () -> ticketService.assign(ticketId, technicianId)
        );
    }

    @Test
    void shouldRejectTicketCreationWhenRequesterIsDisabled() {

        Long requesterId = 1L;

        User requester = mock(User.class);

        when(userRepository.findById(requesterId))
                .thenReturn(Optional.of(requester));

        when(requester.getId())
                .thenReturn(requesterId);

        when(requester.isEnabled())
                .thenReturn(false);

        assertThrows(
                UserDisabledException.class,
                () -> ticketService.create(
                        "Problema de acceso",
                        "No puedo acceder a la aplicación",
                        TicketPriority.HIGH,
                        requesterId
                )
        );

        verify(ticketRepository, never())
                .save(any(Ticket.class));
    }

    @Test
    void shouldAssignActiveTechnicianToTicket() {

        Long ticketId = 4L;
        Long technicianId = 2L;

        Ticket ticket = mock(Ticket.class);
        User technician = mock(User.class);

        when(ticketRepository.findById(ticketId))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        when(technician.getRole())
                .thenReturn(UserRole.TECHNICIAN);

        when(technician.isEnabled())
                .thenReturn(true);

        ticketService.assign(ticketId, technicianId);

        verify(ticket)
                .assignTo(technician);
    }
}