package com.irinakomarchenko.messagingplatform.messaging.mapper;

import com.irinakomarchenko.messagingplatform.messaging.dto.response.DirectMessageResponse;
import com.irinakomarchenko.messagingplatform.messaging.entity.DirectMessage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DirectMessageMapper {

    DirectMessageResponse toResponse(DirectMessage message);
}
