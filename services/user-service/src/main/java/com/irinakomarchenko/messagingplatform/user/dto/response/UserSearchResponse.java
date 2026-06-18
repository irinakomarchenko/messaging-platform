package com.irinakomarchenko.messagingplatform.user.dto.response;

public record UserSearchResponse(
        Long id,
        String fullName,
        String username
) {
}
