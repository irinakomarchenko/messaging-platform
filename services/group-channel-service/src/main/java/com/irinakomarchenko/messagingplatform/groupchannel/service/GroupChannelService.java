package com.irinakomarchenko.messagingplatform.groupchannel.service;

import com.irinakomarchenko.messagingplatform.groupchannel.dto.request.AddGroupMemberRequest;
import com.irinakomarchenko.messagingplatform.groupchannel.dto.request.CreateChannelRequest;
import com.irinakomarchenko.messagingplatform.groupchannel.dto.request.CreateGroupRequest;
import com.irinakomarchenko.messagingplatform.groupchannel.dto.request.SubscribeChannelRequest;
import com.irinakomarchenko.messagingplatform.groupchannel.dto.response.ChannelResponse;
import com.irinakomarchenko.messagingplatform.groupchannel.dto.response.ChannelSubscriberResponse;
import com.irinakomarchenko.messagingplatform.groupchannel.dto.response.GroupMemberResponse;
import com.irinakomarchenko.messagingplatform.groupchannel.dto.response.GroupResponse;
import com.irinakomarchenko.messagingplatform.groupchannel.entity.Channel;
import com.irinakomarchenko.messagingplatform.groupchannel.entity.ChannelSubscriber;
import com.irinakomarchenko.messagingplatform.groupchannel.entity.GroupChat;
import com.irinakomarchenko.messagingplatform.groupchannel.entity.GroupMember;
import com.irinakomarchenko.messagingplatform.groupchannel.exception.ConflictException;
import com.irinakomarchenko.messagingplatform.groupchannel.exception.InvalidOperationException;
import com.irinakomarchenko.messagingplatform.groupchannel.exception.ResourceNotFoundException;
import com.irinakomarchenko.messagingplatform.groupchannel.mapper.GroupChannelMapper;
import com.irinakomarchenko.messagingplatform.groupchannel.model.GroupMemberRole;
import com.irinakomarchenko.messagingplatform.groupchannel.repository.ChannelRepository;
import com.irinakomarchenko.messagingplatform.groupchannel.repository.ChannelSubscriberRepository;
import com.irinakomarchenko.messagingplatform.groupchannel.repository.GroupChatRepository;
import com.irinakomarchenko.messagingplatform.groupchannel.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class GroupChannelService {

    private final GroupChatRepository groupChatRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ChannelRepository channelRepository;
    private final ChannelSubscriberRepository channelSubscriberRepository;
    private final GroupChannelMapper mapper;

    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request) {
        GroupChat group = new GroupChat();
        group.setName(normalizeText(request.name()));
        group.setOwnerUserId(request.ownerUserId());

        GroupChat savedGroup = groupChatRepository.save(group);

        GroupMember ownerMember = new GroupMember();
        ownerMember.setGroup(savedGroup);
        ownerMember.setUserId(request.ownerUserId());
        ownerMember.setRole(GroupMemberRole.OWNER);

        groupMemberRepository.save(ownerMember);

        return mapper.toGroupResponse(savedGroup, 1);
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroup(Long groupId) {
        GroupChat group = findGroupById(groupId);
        long memberCount = groupMemberRepository.countByGroup_Id(groupId);

        return mapper.toGroupResponse(group, memberCount);
    }

    @Transactional
    public GroupMemberResponse addGroupMember(Long groupId, AddGroupMemberRequest request) {
        GroupChat group = findGroupById(groupId);

        if (groupMemberRepository.existsByGroup_IdAndUserId(groupId, request.userId())) {
            throw new ConflictException("User already belongs to group: " + request.userId());
        }

        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUserId(request.userId());
        member.setRole(GroupMemberRole.MEMBER);

        GroupMember savedMember = groupMemberRepository.save(member);

        return mapper.toGroupMemberResponse(savedMember);
    }

    @Transactional(readOnly = true)
    public List<GroupMemberResponse> getGroupMembers(Long groupId) {
        findGroupById(groupId);

        return groupMemberRepository.findByGroup_Id(groupId)
                .stream()
                .map(mapper::toGroupMemberResponse)
                .toList();
    }

    @Transactional
    public void removeGroupMember(Long groupId, Long userId) {
        findGroupById(groupId);

        GroupMember member = groupMemberRepository.findByGroup_IdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User " + userId + " is not a member of group " + groupId
                ));

        if (member.getRole() == GroupMemberRole.OWNER) {
            throw new InvalidOperationException("Group owner cannot be removed from the group");
        }

        groupMemberRepository.delete(member);
    }

    @Transactional
    public ChannelResponse createChannel(CreateChannelRequest request) {
        String normalizedChannelName = normalizeChannelName(request.name());

        if (channelRepository.existsByNameIgnoreCase(normalizedChannelName)) {
            throw new ConflictException("Channel name already exists: " + normalizedChannelName);
        }

        Channel channel = new Channel();
        channel.setName(normalizedChannelName);
        channel.setDescription(normalizeNullableText(request.description()));
        channel.setOwnerUserId(request.ownerUserId());

        Channel savedChannel = channelRepository.save(channel);

        ChannelSubscriber ownerSubscriber = new ChannelSubscriber();
        ownerSubscriber.setChannel(savedChannel);
        ownerSubscriber.setUserId(request.ownerUserId());

        channelSubscriberRepository.save(ownerSubscriber);

        return mapper.toChannelResponse(savedChannel, 1);
    }

    @Transactional(readOnly = true)
    public ChannelResponse getChannel(Long channelId) {
        Channel channel = findChannelById(channelId);
        long subscriberCount = channelSubscriberRepository.countByChannel_Id(channelId);

        return mapper.toChannelResponse(channel, subscriberCount);
    }

    @Transactional
    public ChannelSubscriberResponse subscribeToChannel(
            Long channelId,
            SubscribeChannelRequest request
    ) {
        Channel channel = findChannelById(channelId);

        if (channelSubscriberRepository.existsByChannel_IdAndUserId(channelId, request.userId())) {
            throw new ConflictException("User already subscribed to channel: " + request.userId());
        }

        ChannelSubscriber subscriber = new ChannelSubscriber();
        subscriber.setChannel(channel);
        subscriber.setUserId(request.userId());

        ChannelSubscriber savedSubscriber = channelSubscriberRepository.save(subscriber);

        return mapper.toChannelSubscriberResponse(savedSubscriber);
    }

    @Transactional(readOnly = true)
    public List<ChannelSubscriberResponse> getChannelSubscribers(Long channelId) {
        findChannelById(channelId);

        return channelSubscriberRepository.findByChannel_Id(channelId)
                .stream()
                .map(mapper::toChannelSubscriberResponse)
                .toList();
    }

    @Transactional
    public void unsubscribeFromChannel(Long channelId, Long userId) {
        Channel channel = findChannelById(channelId);

        if (channel.getOwnerUserId().equals(userId)) {
            throw new InvalidOperationException("Channel owner cannot unsubscribe from own channel");
        }

        ChannelSubscriber subscriber = channelSubscriberRepository
                .findByChannel_IdAndUserId(channelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User " + userId + " is not subscribed to channel " + channelId
                ));

        channelSubscriberRepository.delete(subscriber);
    }

    private GroupChat findGroupById(Long groupId) {
        return groupChatRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Group not found: " + groupId
                ));
    }

    private Channel findChannelById(Long channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Channel not found: " + channelId
                ));
    }

    private String normalizeText(String value) {
        return value.trim();
    }

    private String normalizeNullableText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String normalizeChannelName(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
