package com.prix.homepage.backend.user.repository;

import com.prix.homepage.backend.user.domain.User;
import com.prix.homepage.backend.user.domain.UserRole;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TempUserRepository {
    public Optional<User> findByLoginId(String loginId) {

        if (loginId.equals("user1")) {
            return Optional.ofNullable(User.builder()
                    .id(1L)
                    .loginId("user1")
                    .password("asdf")
                    .nickname("nickname")
                    .role(UserRole.USER)
                    .build());
        }
        else if (loginId.equals("admin")) {
            return Optional.ofNullable(User.builder()
                    .id(2L)
                    .loginId("admin")
                    .password("asdf")
                    .nickname("nickname")
                    .role(UserRole.ADMIN)
                    .build());
        }
        else {
            return Optional.empty();
        }
    }

    public boolean save(User user) {
        return true;
    }
}

