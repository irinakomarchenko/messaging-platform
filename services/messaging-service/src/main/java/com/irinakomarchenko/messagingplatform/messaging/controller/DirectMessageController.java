package com.irinakomarchenko.messagingplatform.messaging.controller;

import com.irinakomarchenko.messagingplatform.messaging.dto.request.SendDirectMessageRequest;
import com.irinakomarchenko.messagingplatform.messaging.dto.response.DirectMessageResponse;
import com.irinakomarchenko.messagingplatform.messaging.service.DirectMessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
public class DirectMessageController {

    private final DirectMessageService directMessageService;

    @PostMapping("/direct")
    public DirectMessageResponse sendDirectMessage(@Valid @RequestBody SendDirectMessageRequest request) {
        return directMessageService.sendDirectMessage(request);
    }

    @GetMapping("/direct/{id}")
    public DirectMessageResponse getDirectMessageById(
            @PathVariable @Positive Long id,
            @RequestParam @Positive Long userId
    ) {
        return directMessageService.getDirectMessageById(id, userId);
    }

    @GetMapping("/conversations")
    public List<DirectMessageResponse> getConversation(
            @RequestParam @Positive Long userId,
            @RequestParam @Positive Long otherUserId
    ) {
        return directMessageService.getConversation(userId, otherUserId);
    }

    @PatchMapping("/direct/{id}/read")
    public DirectMessageResponse markMessageAsRead(
            @PathVariable @Positive Long id,
            @RequestParam @Positive Long userId
    ) {
        return directMessageService.markMessageAsRead(id, userId);
    }
}
