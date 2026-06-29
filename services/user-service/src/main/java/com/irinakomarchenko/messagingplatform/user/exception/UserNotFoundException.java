package com.irinakomarchenko.messagingplatform.user.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId);
    }

    public static UserNotFoundException byUsername(String username) {
        return new UserNotFoundException("User not found with username: " + username);
    }

    private UserNotFoundException(String message) {
        super(message);
    }
}
