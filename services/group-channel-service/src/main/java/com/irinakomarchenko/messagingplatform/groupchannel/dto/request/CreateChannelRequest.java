package com.irinakomarchenko.messagingplatform.groupchannel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateChannelRequest(
        @NotBlank(message = "Channel name must not be blank")
        @Size(min = 3, max = 50, message = "Channel name must be between 3 and 50 characters")
        @Pattern(
                regexp = "^[a-zA-Z0-9_-]+$",
                message = "Channel name may contain only letters, numbers, underscores and hyphens"
        )
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotNull(message = "Owner user id must not be null")
        Long ownerUserId
) {
}
