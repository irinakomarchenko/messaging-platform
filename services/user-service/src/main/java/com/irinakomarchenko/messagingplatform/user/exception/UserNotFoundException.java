package com.irinakomarchenko.messagingplatform.user.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("User with id " + userId + " was not found");
    }

    public UserNotFoundException(String username) {
        super("User with username '" + username + "' was not found");
    }
}
