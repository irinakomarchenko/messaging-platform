package com.irinakomarchenko.messagingplatform.groupchannel.dto.response;

import java.time.LocalDateTime;

public record ChannelResponse(
        Long id,
        String name,
        String description,
        Long ownerUserId,
        long subscriberCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
