package com.irinakomarchenko.messagingplatform.user.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String fullName,
        String username,
        LocalDateTime createdAt
) {
}
