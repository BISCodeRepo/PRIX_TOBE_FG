package com.prix.homepage.backend.user.dto;

import com.prix.homepage.backend.user.domain.User;
import com.prix.homepage.backend.user.domain.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestLoginDto {

    private String loginId;
    private String password;
}