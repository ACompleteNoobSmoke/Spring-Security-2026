package com.noobsmoke.springsecurity2026.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LoginResponse {

    private String token;
    private long expiresIn;
}
