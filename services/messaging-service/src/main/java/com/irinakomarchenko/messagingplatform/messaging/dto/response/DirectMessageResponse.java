package com.irinakomarchenko.messagingplatform.messaging.dto.response;

import com.irinakomarchenko.messagingplatform.messaging.model.enums.MessageStatus;

import java.time.Instant;

public record DirectMessageResponse(
        Long id,
        Long senderUserId,
        Long recipientUserId,
        String content,
        MessageStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
