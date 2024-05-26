package com.prix.homepage.backend.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestLoginDto {

    private String email;
    private String password;
}