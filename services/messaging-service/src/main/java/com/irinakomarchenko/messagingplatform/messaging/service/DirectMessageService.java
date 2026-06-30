package com.irinakomarchenko.messagingplatform.messaging.service;

import com.irinakomarchenko.messagingplatform.messaging.dto.request.SendDirectMessageRequest;
import com.irinakomarchenko.messagingplatform.messaging.dto.response.DirectMessageResponse;
import com.irinakomarchenko.messagingplatform.messaging.entity.DirectMessage;
import com.irinakomarchenko.messagingplatform.messaging.exception.InvalidMessageException;
import com.irinakomarchenko.messagingplatform.messaging.exception.MessageAccessDeniedException;
import com.irinakomarchenko.messagingplatform.messaging.exception.MessageNotFoundException;
import com.irinakomarchenko.messagingplatform.messaging.mapper.DirectMessageMapper;
import com.irinakomarchenko.messagingplatform.messaging.model.enums.MessageStatus;
import com.irinakomarchenko.messagingplatform.messaging.repository.DirectMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectMessageService {

    private static final int MAX_MESSAGE_LENGTH = 2000;

    private final DirectMessageRepository directMessageRepository;
    private final DirectMessageMapper directMessageMapper;

    @Transactional
    public DirectMessageResponse sendDirectMessage(SendDirectMessageRequest request) {
        validateDifferentUsers(request.senderUserId(), request.recipientUserId());

        String normalizedContent = normalizeContent(request.content());

        DirectMessage message = DirectMessage.builder()
                .senderUserId(request.senderUserId())
                .recipientUserId(request.recipientUserId())
                .content(normalizedContent)
                .status(MessageStatus.SENT)
                .build();

        DirectMessage savedMessage = directMessageRepository.save(message);

        log.info(
                "Direct message {} sent from user {} to user {}",
                savedMessage.getId(),
                savedMessage.getSenderUserId(),
                savedMessage.getRecipientUserId()
        );

        return directMessageMapper.toResponse(savedMessage);
    }

    @Transactional(readOnly = true)
    public DirectMessageResponse getDirectMessageById(Long id, Long userId) {
        DirectMessage message = findMessageById(id);

        validateMessageParticipant(message, userId);

        return directMessageMapper.toResponse(message);
    }

    @Transactional(readOnly = true)
    public List<DirectMessageResponse> getConversation(Long userId, Long otherUserId) {
        validateDifferentUsers(userId, otherUserId);

        List<DirectMessage> messages = directMessageRepository.findConversation(userId, otherUserId);

        log.debug(
                "Retrieved {} direct messages for conversation between user {} and user {}",
                messages.size(),
                userId,
                otherUserId
        );

        return messages.stream()
                .map(directMessageMapper::toResponse)
                .toList();
    }

    @Transactional
    public DirectMessageResponse markMessageAsRead(Long id, Long userId) {
        DirectMessage message = findMessageById(id);

        validateMessageRecipient(message, userId);

        if (message.getStatus() == MessageStatus.READ) {
            log.debug("Direct message {} is already marked as READ", id);
            return directMessageMapper.toResponse(message);
        }

        message.setStatus(MessageStatus.READ);

        DirectMessage updatedMessage = directMessageRepository.save(message);

        log.info("Direct message {} marked as READ by user {}", id, userId);

        return directMessageMapper.toResponse(updatedMessage);
    }

    private DirectMessage findMessageById(Long id) {
        return directMessageRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Direct message was not found. id={}", id);
                    return new MessageNotFoundException(id);
                });
    }

    private void validateDifferentUsers(Long senderUserId, Long recipientUserId) {
        if (senderUserId.equals(recipientUserId)) {
            log.warn("User {} attempted to send a direct message to themselves", senderUserId);
            throw new InvalidMessageException("Sender and recipient must be different users");
        }
    }

    private String normalizeContent(String content) {
        if (content == null) {
            log.warn("Attempt to send direct message with null content");
            throw new InvalidMessageException("Message content must not be null");
        }

        String normalizedContent = content.trim();

        if (normalizedContent.isBlank()) {
            log.warn("Attempt to send blank direct message");
            throw new InvalidMessageException("Message content must not be blank");
        }

        if (normalizedContent.length() > MAX_MESSAGE_LENGTH) {
            log.warn(
                    "Direct message content exceeds max length. actualLength={}, maxLength={}",
                    normalizedContent.length(),
                    MAX_MESSAGE_LENGTH
            );

            throw new InvalidMessageException(
                    "Message content must not exceed %d characters".formatted(MAX_MESSAGE_LENGTH)
            );
        }

        return normalizedContent;
    }

    private void validateMessageRecipient(DirectMessage message, Long userId) {
        if (!message.getRecipientUserId().equals(userId)) {
            log.warn(
                    "User {} attempted to mark direct message {} as READ, but recipient is {}",
                    userId,
                    message.getId(),
                    message.getRecipientUserId()
            );

            throw new MessageAccessDeniedException("Only the message recipient can mark it as read");
        }
    }

    private void validateMessageParticipant(DirectMessage message, Long userId) {
        boolean isSender = message.getSenderUserId().equals(userId);
        boolean isRecipient = message.getRecipientUserId().equals(userId);

        if (!isSender && !isRecipient) {
            log.warn(
                    "User {} attempted to access direct message {}, but is not a participant",
                    userId,
                    message.getId()
            );

            throw new MessageAccessDeniedException("Only message participants can access this message");
        }
    }
}
