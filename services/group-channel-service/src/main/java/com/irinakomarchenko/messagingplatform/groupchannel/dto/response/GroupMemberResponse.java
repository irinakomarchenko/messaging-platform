package com.irinakomarchenko.messagingplatform.groupchannel.dto.response;

import com.irinakomarchenko.messagingplatform.groupchannel.model.GroupMemberRole;

import java.time.LocalDateTime;

public record GroupMemberResponse(
        Long id,
        Long groupId,
        Long userId,
        GroupMemberRole role,
        LocalDateTime joinedAt
) {
}
