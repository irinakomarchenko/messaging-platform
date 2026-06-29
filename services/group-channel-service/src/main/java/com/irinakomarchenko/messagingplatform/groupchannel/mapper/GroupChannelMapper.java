package com.irinakomarchenko.messagingplatform.groupchannel.mapper;

import com.irinakomarchenko.messagingplatform.groupchannel.config.CentralMapperConfig;
import com.irinakomarchenko.messagingplatform.groupchannel.dto.response.ChannelResponse;
import com.irinakomarchenko.messagingplatform.groupchannel.dto.response.ChannelSubscriberResponse;
import com.irinakomarchenko.messagingplatform.groupchannel.dto.response.GroupMemberResponse;
import com.irinakomarchenko.messagingplatform.groupchannel.dto.response.GroupResponse;
import com.irinakomarchenko.messagingplatform.groupchannel.entity.Channel;
import com.irinakomarchenko.messagingplatform.groupchannel.entity.ChannelSubscriber;
import com.irinakomarchenko.messagingplatform.groupchannel.entity.GroupChat;
import com.irinakomarchenko.messagingplatform.groupchannel.entity.GroupMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface GroupChannelMapper {

    @Mapping(target = "memberCount", source = "memberCount")
    GroupResponse toGroupResponse(GroupChat group, long memberCount);

    @Mapping(target = "groupId", source = "group.id")
    GroupMemberResponse toGroupMemberResponse(GroupMember member);

    @Mapping(target = "subscriberCount", source = "subscriberCount")
    ChannelResponse toChannelResponse(Channel channel, long subscriberCount);

    @Mapping(target = "channelId", source = "channel.id")
    ChannelSubscriberResponse toChannelSubscriberResponse(ChannelSubscriber subscriber);
}
