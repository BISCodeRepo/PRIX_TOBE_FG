package com.prix.homepage.backend.user.repository;

import com.prix.homepage.backend.user.domain.User;
import com.prix.homepage.backend.user.domain.UserRole;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TempUserRepository {
    public Optional<User> findByLoginId(String loginId) {
        return Optional.ofNullable(User.builder()
                .id(1L)
                .loginId("user123")
                .password("securepassword")
                .nickname("nickname")
                .role(UserRole.ADMIN)
                .build());
    }

    public boolean save(User user) {
        return true;
    }
}

