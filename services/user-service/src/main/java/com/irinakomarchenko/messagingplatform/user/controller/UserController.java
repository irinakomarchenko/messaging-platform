package com.irinakomarchenko.messagingplatform.user.controller;

import com.irinakomarchenko.messagingplatform.user.dto.response.UserResponse;
import com.irinakomarchenko.messagingplatform.user.dto.response.UserSearchResponse;
import com.irinakomarchenko.messagingplatform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/search")
    public UserSearchResponse searchByUsername(@RequestParam String username) {
        return userService.searchByUsername(username);
    }
}
