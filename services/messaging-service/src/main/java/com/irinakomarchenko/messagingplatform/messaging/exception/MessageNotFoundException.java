package com.irinakomarchenko.messagingplatform.messaging.exception;

public class MessageNotFoundException extends RuntimeException {

    public MessageNotFoundException(Long id) {
        super("Message with id %d was not found".formatted(id));
    }
}
