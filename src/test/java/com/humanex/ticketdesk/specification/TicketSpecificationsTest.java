package com.humanex.ticketdesk.specification;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import com.humanex.ticketdesk.entity.Ticket;
import com.humanex.ticketdesk.entity.User;
import com.humanex.ticketdesk.enums.TicketPriority;
import com.humanex.ticketdesk.enums.TicketStatus;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

class TicketSpecificationsTest {

    @Test
    @SuppressWarnings("unchecked")
    void shouldFilterByStatus() {

        Root<Ticket> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder =
                mock(CriteriaBuilder.class);

        Path<TicketStatus> statusPath = mock(Path.class);
        Predicate expectedPredicate = mock(Predicate.class);

        when(root.<TicketStatus>get("status"))
                .thenReturn(statusPath);

        when(criteriaBuilder.equal(
                statusPath,
                TicketStatus.OPEN
        )).thenReturn(expectedPredicate);

        Specification<Ticket> specification =
                TicketSpecifications.hasStatus(
                        TicketStatus.OPEN
                );

        Predicate result = specification.toPredicate(
                root,
                query,
                criteriaBuilder
        );

        assertSame(expectedPredicate, result);

        verify(criteriaBuilder).equal(
                statusPath,
                TicketStatus.OPEN
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldFilterByPriority() {

        Root<Ticket> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder =
                mock(CriteriaBuilder.class);

        Path<TicketPriority> priorityPath =
                mock(Path.class);

        Predicate expectedPredicate = mock(Predicate.class);

        when(root.<TicketPriority>get("priority"))
                .thenReturn(priorityPath);

        when(criteriaBuilder.equal(
                priorityPath,
                TicketPriority.HIGH
        )).thenReturn(expectedPredicate);

        Specification<Ticket> specification =
                TicketSpecifications.hasPriority(
                        TicketPriority.HIGH
                );

        Predicate result = specification.toPredicate(
                root,
                query,
                criteriaBuilder
        );

        assertSame(expectedPredicate, result);

        verify(criteriaBuilder).equal(
                priorityPath,
                TicketPriority.HIGH
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldFilterByRequesterId() {

        Root<Ticket> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder =
                mock(CriteriaBuilder.class);

        Path<User> requesterPath = mock(Path.class);
        Path<Long> requesterIdPath = mock(Path.class);

        Predicate expectedPredicate = mock(Predicate.class);

        when(root.<User>get("requester"))
                .thenReturn(requesterPath);

        when(requesterPath.<Long>get("id"))
                .thenReturn(requesterIdPath);

        when(criteriaBuilder.equal(
                requesterIdPath,
                1L
        )).thenReturn(expectedPredicate);

        Specification<Ticket> specification =
                TicketSpecifications.hasRequesterId(1L);

        Predicate result = specification.toPredicate(
                root,
                query,
                criteriaBuilder
        );

        assertSame(expectedPredicate, result);

        verify(criteriaBuilder).equal(
                requesterIdPath,
                1L
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldNotRestrictWhenFilterIsNull() {

        Root<Ticket> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder =
                mock(CriteriaBuilder.class);

        Predicate conjunction = mock(Predicate.class);

        when(criteriaBuilder.conjunction())
                .thenReturn(conjunction);

        Specification<Ticket> specification =
                TicketSpecifications.hasStatus(null);

        Predicate result = specification.toPredicate(
                root,
                query,
                criteriaBuilder
        );

        assertSame(conjunction, result);

        verify(criteriaBuilder).conjunction();
    }
}