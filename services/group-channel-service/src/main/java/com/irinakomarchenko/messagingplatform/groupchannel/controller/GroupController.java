package com.irinakomarchenko.messagingplatform.groupchannel.controller;

import com.irinakomarchenko.messagingplatform.groupchannel.dto.request.AddGroupMemberRequest;
import com.irinakomarchenko.messagingplatform.groupchannel.dto.request.CreateGroupRequest;
import com.irinakomarchenko.messagingplatform.groupchannel.dto.response.GroupMemberResponse;
import com.irinakomarchenko.messagingplatform.groupchannel.dto.response.GroupResponse;
import com.irinakomarchenko.messagingplatform.groupchannel.service.GroupChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final GroupChannelService groupChannelService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponse createGroup(@Valid @RequestBody CreateGroupRequest request) {
        return groupChannelService.createGroup(request);
    }

    @GetMapping("/{groupId}")
    public GroupResponse getGroup(@PathVariable Long groupId) {
        return groupChannelService.getGroup(groupId);
    }

    @PostMapping("/{groupId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public GroupMemberResponse addGroupMember(
            @PathVariable Long groupId,
            @Valid @RequestBody AddGroupMemberRequest request
    ) {
        return groupChannelService.addGroupMember(groupId, request);
    }

    @GetMapping("/{groupId}/members")
    public List<GroupMemberResponse> getGroupMembers(@PathVariable Long groupId) {
        return groupChannelService.getGroupMembers(groupId);
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGroupMember(
            @PathVariable Long groupId,
            @PathVariable Long userId
    ) {
        groupChannelService.removeGroupMember(groupId, userId);
    }
}
