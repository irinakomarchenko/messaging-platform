package com.irinakomarchenko.messagingplatform.user.dto.response;

public record AuthResponse(
        Long userId,
        String username,
        String authToken
) {
}
