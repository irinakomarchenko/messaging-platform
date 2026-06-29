package com.irinakomarchenko.messagingplatform.groupchannel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateGroupRequest(
        @NotBlank(message = "Group name must not be blank")
        @Size(min = 2, max = 100, message = "Group name must be between 2 and 100 characters")
        String name,

        @NotNull(message = "Owner user id must not be null")
        Long ownerUserId
) {
}
