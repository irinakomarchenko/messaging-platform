package com.irinakomarchenko.messagingplatform.messaging.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SendDirectMessageRequest(

        @NotNull(message = "Sender user id is required")
        @Positive(message = "Sender user id must be positive")
        Long senderUserId,

        @NotNull(message = "Recipient user id is required")
        @Positive(message = "Recipient user id must be positive")
        Long recipientUserId,

        @NotBlank(message = "Message content must not be blank")
        @Size(max = 2000, message = "Message content must not exceed 2000 characters")
        String content
) {
}
