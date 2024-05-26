package com.prix.homepage.backend.user.controller;

import com.prix.homepage.backend.basic.controller.JwtUtil;
import com.prix.homepage.backend.user.domain.User;
import com.prix.homepage.backend.user.dto.RequestLoginDto;
import com.prix.homepage.backend.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class ApiLoginController {

    // UserService 의존성 주입을 위한 필드 선언
    @Autowired
    private final UserService userService;

    // ApiLoginController 생성자
    @Autowired
    public ApiLoginController(UserService userService) {
        this.userService = userService;
    }

    /**
     사용자 로그인 메소드

     @param requestLoginDto 로그인 요청 DTO
     @param level 사용자의 역할 레벨
     @return JWT 토큰 또는 오류 메시지
     */
    @ApiOperation(value = "로그인", notes = "사용자 혹은 관리자의 로그인을 합니다. 값 유효성 검사를 포함합니다.")
    @PostMapping("/user/login")
    public String userLogin(@RequestBody RequestLoginDto requestLoginDto, @RequestParam int level) {
        // 사용자 로그인 처리
        User user = userService.login(requestLoginDto, level);

        // JWT 토큰 생성
        String secretKey = "secret-key-1234125";
        long expireTimeMs = 1000 * 60 * 60;

        String jwtToken = JwtUtil.createToken(user.getEmail(), user.getLevel(), secretKey, expireTimeMs);

        return jwtToken;
    }

    /**
     사용자 회원가입 메소드

     @param requestLoginDto 회원가입 요청 DTO
     @return 성공 메시지
     */
    @ApiOperation(value = "회원가입", notes = "사용자의 회원가입을 합니다. 값 유효성 검사를 포함합니다.")
    @PostMapping("/user/signup")
    public String userSignUp(@RequestBody RequestLoginDto requestLoginDto) {
        // 사용자 회원가입 처리
        userService.signUp(requestLoginDto);

        return "Signup completed successfully.";
    }
}
