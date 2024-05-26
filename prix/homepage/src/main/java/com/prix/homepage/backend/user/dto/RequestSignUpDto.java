package com.prix.homepage.backend.user.dto;

import com.prix.homepage.backend.user.domain.User;
import com.prix.homepage.backend.user.domain.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestSignUpDto {

    private String loginId;
    private String password;
    private String nickname;

    // 비밀번호 암호화 X
    public User toEntity() {
        return User.builder()
                .email(this.loginId)
                .password(this.password)
                .name(this.nickname)
                .build();
    }

    // 비밀번호 암호화
    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(this.loginId)
                .password(encodedPassword)
                .name(this.nickname)
                .build();
    }
}