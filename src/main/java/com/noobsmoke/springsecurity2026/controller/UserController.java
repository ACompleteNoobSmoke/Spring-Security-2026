package com.noobsmoke.springsecurity2026.controller;

import com.noobsmoke.springsecurity2026.model.User;
import com.noobsmoke.springsecurity2026.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok((User) authentication.getPrincipal());
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> allUsers() {
        return ResponseEntity.ok(userService.allUsers());
    }
}
