package com.irinakomarchenko.messagingplatform.groupchannel.dto.response;

import java.time.LocalDateTime;

public record ChannelSubscriberResponse(
        Long id,
        Long channelId,
        Long userId,
        LocalDateTime subscribedAt
) {
}
