package com.prix.homepage.backend.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {


    private Long id;

    private String loginId;
    private String password;
    private String nickname;
    private UserRole role;
}