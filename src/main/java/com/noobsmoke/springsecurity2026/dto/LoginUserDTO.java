package com.noobsmoke.springsecurity2026.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginUserDTO {
    private String username;
    private String password;
}
