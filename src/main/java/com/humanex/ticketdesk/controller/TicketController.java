package com.humanex.ticketdesk.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.humanex.ticketdesk.dto.CreateTicketRequest;
import com.humanex.ticketdesk.dto.TicketResponse;
import com.humanex.ticketdesk.dto.TicketStatusHistoryResponse;
import com.humanex.ticketdesk.dto.UpdateTicketStatusRequest;
import com.humanex.ticketdesk.entity.Ticket;
import com.humanex.ticketdesk.entity.TicketStatusHistory;
import com.humanex.ticketdesk.service.TicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<TicketResponse> findAll() {

        return ticketService.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> findById(
            @PathVariable Long id) {

        Ticket ticket = ticketService.findById(id);

        return ResponseEntity.ok(
                toResponse(ticket)
        );
    }

    @GetMapping("/{id}/history")
    public List<TicketStatusHistoryResponse> findHistory(
            @PathVariable Long id) {

        return ticketService.findHistoryByTicketId(id)
                .stream()
                .map(this::toHistoryResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(
            @Valid @RequestBody CreateTicketRequest request) {

        Ticket ticket = ticketService.create(
                request.title(),
                request.description(),
                request.priority(),
                request.requesterId()
        );

        TicketResponse response = toResponse(ticket);

        URI location = URI.create(
                "/api/tickets/" + response.id()
        );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketStatusRequest request) {

        Ticket ticket = ticketService.updateStatus(
                id,
                request.status()
        );

        return ResponseEntity.ok(
                toResponse(ticket)
        );
    }

    private TicketResponse toResponse(Ticket ticket) {

        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }

    private TicketStatusHistoryResponse toHistoryResponse(
            TicketStatusHistory history) {

        return new TicketStatusHistoryResponse(
                history.getId(),
                history.getOldStatus(),
                history.getNewStatus(),
                history.getChangedAt()
        );
    }
}