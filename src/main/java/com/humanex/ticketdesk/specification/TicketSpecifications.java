package com.humanex.ticketdesk.specification;

import org.springframework.data.jpa.domain.Specification;

import com.humanex.ticketdesk.entity.Ticket;
import com.humanex.ticketdesk.enums.TicketPriority;
import com.humanex.ticketdesk.enums.TicketStatus;

public final class TicketSpecifications {

    private TicketSpecifications() {
    }

    public static Specification<Ticket> hasStatus(
            TicketStatus status) {

        return (root, query, criteriaBuilder) -> {

            if (status == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("status"),
                    status
            );
        };
    }

    public static Specification<Ticket> hasPriority(
            TicketPriority priority) {

        return (root, query, criteriaBuilder) -> {

            if (priority == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("priority"),
                    priority
            );
        };
    }

    public static Specification<Ticket> hasRequesterId(
            Long requesterId) {

        return (root, query, criteriaBuilder) -> {

            if (requesterId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("requester").get("id"),
                    requesterId
            );
        };
    }
}