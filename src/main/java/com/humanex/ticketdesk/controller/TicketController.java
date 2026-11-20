package com.humanex.ticketdesk.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.humanex.ticketdesk.dto.AssignTicketRequest;
import com.humanex.ticketdesk.dto.CreateTicketRequest;
import com.humanex.ticketdesk.dto.PageResponse;
import com.humanex.ticketdesk.dto.TicketResponse;
import com.humanex.ticketdesk.dto.TicketStatusHistoryResponse;
import com.humanex.ticketdesk.dto.UpdateTicketStatusRequest;
import com.humanex.ticketdesk.dto.UserSummaryResponse;
import com.humanex.ticketdesk.entity.Ticket;
import com.humanex.ticketdesk.entity.TicketStatusHistory;
import com.humanex.ticketdesk.entity.User;
import com.humanex.ticketdesk.enums.TicketPriority;
import com.humanex.ticketdesk.enums.TicketStatus;
import com.humanex.ticketdesk.service.TicketService;
import com.humanex.ticketdesk.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;

    public TicketController(
            TicketService ticketService,
            UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @GetMapping
    public PageResponse<TicketResponse> findAll(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        User authenticatedUser = getAuthenticatedUser(jwt);

        Page<Ticket> ticketPage =
                ticketService.findAllVisibleTo(
                        authenticatedUser.getId(),
                        authenticatedUser.getRole(),
                        status,
                        priority,
                        pageable
                );

        List<TicketResponse> content =
                ticketPage.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return new PageResponse<>(
                content,
                ticketPage.getNumber(),
                ticketPage.getSize(),
                ticketPage.getTotalElements(),
                ticketPage.getTotalPages(),
                ticketPage.isFirst(),
                ticketPage.isLast()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        User authenticatedUser = getAuthenticatedUser(jwt);

        Ticket ticket =
                ticketService.findByIdVisibleTo(
                        id,
                        authenticatedUser.getId(),
                        authenticatedUser.getRole()
                );

        return ResponseEntity.ok(
                toResponse(ticket)
        );
    }

    @GetMapping("/{id}/history")
    public List<TicketStatusHistoryResponse> findHistory(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        User authenticatedUser = getAuthenticatedUser(jwt);

        return ticketService.findHistoryVisibleTo(
                        id,
                        authenticatedUser.getId(),
                        authenticatedUser.getRole()
                )
                .stream()
                .map(this::toHistoryResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(
            @Valid @RequestBody CreateTicketRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        User authenticatedUser = getAuthenticatedUser(jwt);

        Ticket ticket =
                ticketService.create(
                        request.title(),
                        request.description(),
                        request.priority(),
                        authenticatedUser.getId()
                );

        TicketResponse response = toResponse(ticket);

        URI location =
                URI.create(
                        "/api/tickets/" + response.id()
                );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PatchMapping("/{id}/assignee")
    public ResponseEntity<TicketResponse> assign(
            @PathVariable Long id,
            @Valid @RequestBody AssignTicketRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        User authenticatedUser = getAuthenticatedUser(jwt);

        Ticket ticket =
                ticketService.assignAuthorized(
                        id,
                        request.assigneeId(),
                        authenticatedUser.getId(),
                        authenticatedUser.getRole()
                );

        return ResponseEntity.ok(
                toResponse(ticket)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketStatusRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        User authenticatedUser = getAuthenticatedUser(jwt);

        Ticket ticket =
                ticketService.updateStatusAuthorized(
                        id,
                        request.status(),
                        authenticatedUser.getId(),
                        authenticatedUser.getRole()
                );

        return ResponseEntity.ok(
                toResponse(ticket)
        );
    }

    private User getAuthenticatedUser(Jwt jwt) {
        return userService.findByEmail(
                jwt.getSubject()
        );
    }

    private TicketResponse toResponse(
            Ticket ticket) {

        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                toUserSummary(
                        ticket.getRequester()
                ),
                toUserSummary(
                        ticket.getAssignee()
                ),
                ticket.getSlaDueAt(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }

    private UserSummaryResponse toUserSummary(
            User user) {

        if (user == null) {
            return null;
        }

        return new UserSummaryResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    private TicketStatusHistoryResponse
            toHistoryResponse(
                    TicketStatusHistory history) {

        return new TicketStatusHistoryResponse(
                history.getId(),
                history.getOldStatus(),
                history.getNewStatus(),
                history.getChangedAt()
        );
    }
}