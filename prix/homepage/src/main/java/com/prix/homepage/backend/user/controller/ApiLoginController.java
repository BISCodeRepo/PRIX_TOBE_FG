package com.prix.homepage.backend.user.controller;

import com.prix.homepage.backend.basic.controller.JwtUtil;
import com.prix.homepage.backend.user.domain.User;
import com.prix.homepage.backend.user.domain.UserRole;
import com.prix.homepage.backend.user.dto.RequestLoginDto;
import com.prix.homepage.backend.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/login")
public class ApiLoginController {

    @Autowired
    private final UserService userService;

    @Autowired
    public ApiLoginController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "테스트", notes = "스웨거 동작 테스트입니다.")
    @GetMapping("/test")
    public String test() {
        return "Hello this is test";
    }

    @ApiOperation(value = "로그인", notes = "사용자 혹은 관리자의 로그인을 합니다. 값 유효성 검사를 포함합니다.")
    @PostMapping("/user/login")
    public String userLogin( @RequestBody RequestLoginDto requestLoginDto, @RequestParam int level) {

        User user = userService.login(requestLoginDto);

        // 사용자의 역할 확인
        if (!(user.getLevel() == level)) {
            return "사용자의 역할이 다릅니다.";
        }

        String secretKey = "secret-key-1234125";
        long expireTimeMs = 1000 * 60 * 60;

        String jwtToken = JwtUtil.createToken(user.getEmail(), user.getLevel(), secretKey, expireTimeMs);

        return jwtToken;
    }

    @ApiOperation(value = "회원가입", notes = "사용자의 회원가입을 합니다. 값 유효성 검사를 포함합니다.")
    @PostMapping("/user/signup")
    public String userSignUp(@RequestBody RequestLoginDto requestLoginDto) {

        userService.signUp(requestLoginDto);

        return "회원가입이 완료되었습니다.";
    }

    @ApiOperation(value = "테스트", notes = "사용자의 회원가입을 합니다. 값 유효성 검사를 포함합니다.")
    @GetMapping("/user/test")
    public List<User> getAllUsers() {
        return userService.getUserTest();
    }
}
