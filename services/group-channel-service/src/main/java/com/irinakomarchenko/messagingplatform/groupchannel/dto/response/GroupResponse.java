package com.irinakomarchenko.messagingplatform.groupchannel.dto.response;

import java.time.LocalDateTime;

public record GroupResponse(
        Long id,
        String name,
        Long ownerUserId,
        long memberCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
