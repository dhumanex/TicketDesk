package com.humanex.ticketdesk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.humanex.ticketdesk.entity.User;
import com.humanex.ticketdesk.enums.UserRole;
import com.humanex.ticketdesk.exception.EmailAlreadyExistsException;
import com.humanex.ticketdesk.exception.UserNotFoundException;
import com.humanex.ticketdesk.repository.UserRepository;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {

        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        userService = new UserService(
                userRepository,
                passwordEncoder
        );
    }

    @Test
    void shouldRejectUserCreationWhenEmailAlreadyExists() {

        String email = "usuario.test@ticketdesk.local";

        when(userRepository.existsByEmail(email))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.create(
                        "Usuario Test",
                        email,
                        "Password123!",
                        UserRole.USER
                )
        );
    }

    @Test
    void shouldEncodePasswordBeforeSavingUser() {

        String rawPassword = "Password123!";
        String encodedPassword = "$2a$10$encodedPasswordForTest";

        when(userRepository.existsByEmail("nuevo@ticketdesk.local"))
                .thenReturn(false);

        when(passwordEncoder.encode(rawPassword))
                .thenReturn(encodedPassword);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.create(
                "Nuevo Usuario",
                "nuevo@ticketdesk.local",
                rawPassword,
                UserRole.USER
        );

        verify(passwordEncoder)
                .encode(rawPassword);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository)
                .save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(
                encodedPassword,
                savedUser.getPasswordHash()
        );
    }

    @Test
    void shouldDisableExistingUser() {

        Long userId = 1L;

        User user = mock(User.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        User result = userService.disable(userId);

        verify(user)
                .disable();

        assertSame(user, result);
    }

    @Test
    void shouldEnableExistingUser() {

        Long userId = 1L;

        User user = mock(User.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        User result = userService.enable(userId);

        verify(user)
                .enable();

        assertSame(user, result);
    }

    @Test
    void shouldThrowWhenDisablingUnknownUser() {

        Long userId = 999L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.disable(userId)
        );
    }
}