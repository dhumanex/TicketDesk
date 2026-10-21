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

import com.humanex.ticketdesk.dto.CreateUserRequest;
import com.humanex.ticketdesk.dto.UserResponse;
import com.humanex.ticketdesk.entity.User;
import com.humanex.ticketdesk.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> findAll() {

        return userService.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(
            @PathVariable Long id) {

        User user = userService.findById(id);

        return ResponseEntity.ok(
                toResponse(user)
        );
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody CreateUserRequest request) {

        User user = userService.create(
                request.name(),
                request.email(),
                request.password(),
                request.role()
        );

        UserResponse response = toResponse(user);

        URI location = URI.create(
                "/api/users/" + response.id()
        );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<UserResponse> disable(
            @PathVariable Long id) {

        User user = userService.disable(id);

        return ResponseEntity.ok(
                toResponse(user)
        );
    }

    private UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}