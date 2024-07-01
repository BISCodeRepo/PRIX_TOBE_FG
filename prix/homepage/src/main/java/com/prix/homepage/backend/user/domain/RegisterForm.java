package com.prix.homepage.backend.user.domain;

import lombok.Data;

@Data
public class RegisterForm {
    private String username;
    private String password;
    private String confirmPassword;
}