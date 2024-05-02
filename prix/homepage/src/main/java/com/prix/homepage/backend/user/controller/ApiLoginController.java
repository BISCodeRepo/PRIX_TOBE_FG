package com.prix.homepage.backend.user.controller;

import com.prix.homepage.backend.basic.controller.JwtUtil;
import com.prix.homepage.backend.user.domain.User;
import com.prix.homepage.backend.user.domain.UserRole;
import com.prix.homepage.backend.user.dto.RequestLoginDto;
import com.prix.homepage.backend.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class ApiLoginController {

    private final UserService userService;

    @Autowired
    public ApiLoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public String test() {
        return "Hello this is test";
    }

    @PostMapping("/user/login")
    public String userLogin( @RequestBody RequestLoginDto requestLoginDto, @RequestParam UserRole role) {

        User user = userService.login(requestLoginDto);

        if(user == null) {
            return "로그인 아이디 또는 비밀번호가 틀렸습니다.";
        }

        // 사용자의 역할 확인
        if (!user.getRole().equals(role)) {
            return "사용자의 역할이 다릅니다.";
        }

        String secretKey = "secret-key-1234125";
        long expireTimeMs = 1000 * 60 * 60;

        String jwtToken = JwtUtil.createToken(user.getLoginId(), user.getRole(), secretKey, expireTimeMs);

        return jwtToken;
    }
}
