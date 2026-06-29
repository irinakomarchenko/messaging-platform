package com.irinakomarchenko.messagingplatform.groupchannel.controller;

import com.irinakomarchenko.messagingplatform.groupchannel.dto.request.CreateChannelRequest;
import com.irinakomarchenko.messagingplatform.groupchannel.dto.request.SubscribeChannelRequest;
import com.irinakomarchenko.messagingplatform.groupchannel.dto.response.ChannelResponse;
import com.irinakomarchenko.messagingplatform.groupchannel.dto.response.ChannelSubscriberResponse;
import com.irinakomarchenko.messagingplatform.groupchannel.service.GroupChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/channels")
public class ChannelController {

    private final GroupChannelService groupChannelService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelResponse createChannel(@Valid @RequestBody CreateChannelRequest request) {
        return groupChannelService.createChannel(request);
    }

    @GetMapping("/{channelId}")
    public ChannelResponse getChannel(@PathVariable Long channelId) {
        return groupChannelService.getChannel(channelId);
    }

    @PostMapping("/{channelId}/subscribers")
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelSubscriberResponse subscribeToChannel(
            @PathVariable Long channelId,
            @Valid @RequestBody SubscribeChannelRequest request
    ) {
        return groupChannelService.subscribeToChannel(channelId, request);
    }

    @GetMapping("/{channelId}/subscribers")
    public List<ChannelSubscriberResponse> getChannelSubscribers(@PathVariable Long channelId) {
        return groupChannelService.getChannelSubscribers(channelId);
    }

    @DeleteMapping("/{channelId}/subscribers/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribeFromChannel(
            @PathVariable Long channelId,
            @PathVariable Long userId
    ) {
        groupChannelService.unsubscribeFromChannel(channelId, userId);
    }
}
