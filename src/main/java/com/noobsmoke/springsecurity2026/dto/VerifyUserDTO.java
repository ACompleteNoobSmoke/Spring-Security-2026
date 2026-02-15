package com.noobsmoke.springsecurity2026.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyUserDTO {
    private String email;
    private String verificationCode;
    private String username;
}
