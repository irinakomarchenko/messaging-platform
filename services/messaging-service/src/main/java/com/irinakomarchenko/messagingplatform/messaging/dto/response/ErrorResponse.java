package com.irinakomarchenko.messagingplatform.messaging.dto.response;

import java.time.Instant;

public record ErrorResponse(
        int status,
        String message,
        Instant timestamp
) {
}
