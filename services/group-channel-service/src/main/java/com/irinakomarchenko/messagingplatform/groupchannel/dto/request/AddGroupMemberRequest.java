package com.irinakomarchenko.messagingplatform.groupchannel.dto.request;

import jakarta.validation.constraints.NotNull;

public record AddGroupMemberRequest(
        @NotNull(message = "User id must not be null")
        Long userId
) {
}
