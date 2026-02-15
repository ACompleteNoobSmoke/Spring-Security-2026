package com.noobsmoke.springsecurity2026.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDTO {

    private String email;
    private String password;
    private String userName;
}
