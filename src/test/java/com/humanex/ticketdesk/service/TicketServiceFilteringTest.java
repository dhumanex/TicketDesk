package com.humanex.ticketdesk.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.humanex.ticketdesk.entity.Ticket;
import com.humanex.ticketdesk.entity.User;
import com.humanex.ticketdesk.enums.TicketPriority;
import com.humanex.ticketdesk.enums.TicketStatus;
import com.humanex.ticketdesk.enums.UserRole;
import com.humanex.ticketdesk.repository.TicketRepository;
import com.humanex.ticketdesk.repository.TicketStatusHistoryRepository;
import com.humanex.ticketdesk.repository.UserRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

class TicketServiceFilteringTest {

    private final TicketRepository ticketRepository =
            mock(TicketRepository.class);

    private final TicketStatusHistoryRepository
            ticketStatusHistoryRepository =
                    mock(TicketStatusHistoryRepository.class);

    private final UserRepository userRepository =
            mock(UserRepository.class);

    private final TicketService ticketService =
            new TicketService(
                    ticketRepository,
                    ticketStatusHistoryRepository,
                    userRepository
            );

    @Test
    @SuppressWarnings("unchecked")
    void shouldApplyRequesterFilterForUser() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Ticket> expectedPage =
                Page.empty(pageable);

        when(ticketRepository.findAll(
                any(Specification.class),
                eq(pageable)
        )).thenReturn(expectedPage);

        Page<Ticket> result =
                ticketService.findAllVisibleTo(
                        1L,
                        UserRole.USER,
                        TicketStatus.OPEN,
                        TicketPriority.HIGH,
                        pageable
                );

        assertSame(expectedPage, result);

        ArgumentCaptor<Specification<Ticket>> captor =
                ArgumentCaptor.forClass(
                        Specification.class
                );

        verify(ticketRepository).findAll(
                captor.capture(),
                eq(pageable)
        );

        Specification<Ticket> specification =
                captor.getValue();

        Root<Ticket> root = mock(Root.class);
        CriteriaQuery<?> query =
                mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder =
                mock(CriteriaBuilder.class);

        Path<TicketStatus> statusPath =
                mock(Path.class);

        Path<TicketPriority> priorityPath =
                mock(Path.class);

        Path<User> requesterPath =
                mock(Path.class);

        Path<Long> requesterIdPath =
                mock(Path.class);

        Predicate statusPredicate =
                mock(Predicate.class);

        Predicate priorityPredicate =
                mock(Predicate.class);

        Predicate requesterPredicate =
                mock(Predicate.class);

        when(root.<TicketStatus>get("status"))
                .thenReturn(statusPath);

        when(root.<TicketPriority>get("priority"))
                .thenReturn(priorityPath);

        when(root.<User>get("requester"))
                .thenReturn(requesterPath);

        when(requesterPath.<Long>get("id"))
                .thenReturn(requesterIdPath);

        when(criteriaBuilder.equal(
                statusPath,
                TicketStatus.OPEN
        )).thenReturn(statusPredicate);

        when(criteriaBuilder.equal(
                priorityPath,
                TicketPriority.HIGH
        )).thenReturn(priorityPredicate);

        when(criteriaBuilder.equal(
                requesterIdPath,
                1L
        )).thenReturn(requesterPredicate);

        specification.toPredicate(
                root,
                query,
                criteriaBuilder
        );

        verify(criteriaBuilder).equal(
                statusPath,
                TicketStatus.OPEN
        );

        verify(criteriaBuilder).equal(
                priorityPath,
                TicketPriority.HIGH
        );

        verify(criteriaBuilder).equal(
                requesterIdPath,
                1L
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldNotApplyRequesterFilterForAdmin() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Ticket> expectedPage =
                Page.empty(pageable);

        when(ticketRepository.findAll(
                any(Specification.class),
                eq(pageable)
        )).thenReturn(expectedPage);

        Page<Ticket> result =
                ticketService.findAllVisibleTo(
                        3L,
                        UserRole.ADMIN,
                        TicketStatus.RESOLVED,
                        TicketPriority.MEDIUM,
                        pageable
                );

        assertSame(expectedPage, result);

        ArgumentCaptor<Specification<Ticket>> captor =
                ArgumentCaptor.forClass(
                        Specification.class
                );

        verify(ticketRepository).findAll(
                captor.capture(),
                eq(pageable)
        );

        Specification<Ticket> specification =
                captor.getValue();

        Root<Ticket> root = mock(Root.class);
        CriteriaQuery<?> query =
                mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder =
                mock(CriteriaBuilder.class);

        Path<TicketStatus> statusPath =
                mock(Path.class);

        Path<TicketPriority> priorityPath =
                mock(Path.class);

        Predicate statusPredicate =
                mock(Predicate.class);

        Predicate priorityPredicate =
                mock(Predicate.class);

        when(root.<TicketStatus>get("status"))
                .thenReturn(statusPath);

        when(root.<TicketPriority>get("priority"))
                .thenReturn(priorityPath);

        when(criteriaBuilder.equal(
                statusPath,
                TicketStatus.RESOLVED
        )).thenReturn(statusPredicate);

        when(criteriaBuilder.equal(
                priorityPath,
                TicketPriority.MEDIUM
        )).thenReturn(priorityPredicate);

        specification.toPredicate(
                root,
                query,
                criteriaBuilder
        );

        verify(criteriaBuilder).equal(
                statusPath,
                TicketStatus.RESOLVED
        );

        verify(criteriaBuilder).equal(
                priorityPath,
                TicketPriority.MEDIUM
        );

        verify(root, never()).get("requester");
    }
}