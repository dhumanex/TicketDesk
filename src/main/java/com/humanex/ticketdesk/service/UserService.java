package com.humanex.ticketdesk.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanex.ticketdesk.entity.User;
import com.humanex.ticketdesk.enums.UserRole;
import com.humanex.ticketdesk.exception.EmailAlreadyExistsException;
import com.humanex.ticketdesk.exception.UserNotFoundException;
import com.humanex.ticketdesk.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User findByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Transactional
    public User create(
            String name,
            String email,
            String password,
            UserRole role) {

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        String passwordHash =
                passwordEncoder.encode(password);

        User user = new User(
                name,
                email,
                passwordHash,
                role
        );

        return userRepository.save(user);
    }

    @Transactional
    public User disable(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.disable();

        return user;
    }
}