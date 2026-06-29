package com.irinakomarchenko.messagingplatform.user.service;

import com.irinakomarchenko.messagingplatform.user.dto.request.LoginRequest;
import com.irinakomarchenko.messagingplatform.user.dto.request.SignUpRequest;
import com.irinakomarchenko.messagingplatform.user.dto.response.AuthResponse;
import com.irinakomarchenko.messagingplatform.user.dto.response.UserResponse;
import com.irinakomarchenko.messagingplatform.user.dto.response.UserSearchResponse;
import com.irinakomarchenko.messagingplatform.user.entity.User;
import com.irinakomarchenko.messagingplatform.user.exception.InvalidCredentialsException;
import com.irinakomarchenko.messagingplatform.user.exception.UserNotFoundException;
import com.irinakomarchenko.messagingplatform.user.exception.UsernameAlreadyExistsException;
import com.irinakomarchenko.messagingplatform.user.mapper.UserMapper;
import com.irinakomarchenko.messagingplatform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String TEMPORARY_TOKEN_PREFIX = "dev-token-";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse signUp(SignUpRequest request) {
        String normalizedUsername = normalizeUsername(request.username());

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new UsernameAlreadyExistsException(normalizedUsername);
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setUsername(normalizedUsername);
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                generateTemporaryToken(savedUser)
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedUsername = normalizeUsername(request.username());

        User user = userRepository.findByUsername(normalizedUsername)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                generateTemporaryToken(user)
        );
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return userMapper.toUserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserSearchResponse searchByUsername(String username) {
        String normalizedUsername = normalizeUsername(username);

        User user = userRepository.findByUsername(normalizedUsername)
                .orElseThrow(() -> UserNotFoundException.byUsername(normalizedUsername));

        return userMapper.toUserSearchResponse(user);
    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }

    private String generateTemporaryToken(User user) {
        return TEMPORARY_TOKEN_PREFIX + user.getId() + "-" + UUID.randomUUID();
    }
}
