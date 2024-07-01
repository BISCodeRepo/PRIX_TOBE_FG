package com.prix.homepage.backend.user.controller;

import com.prix.homepage.backend.user.domain.User;
import com.prix.homepage.backend.user.dto.RequestLoginDto;
import com.prix.homepage.backend.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/login")
public class ApiLoginController {

    @Autowired
    private final UserService userService;

    @Autowired
    public ApiLoginController(UserService userService) {
        this.userService = userService;
    }

    /**
     사용자 로그인 메소드

     @param requestLoginDto 로그인 요청 DTO
     @param level 사용자의 역할 레벨
     @return 성공 또는 오류 메시지
     */
    @ApiOperation(value = "로그인", notes = "사용자 혹은 관리자의 로그인을 합니다. 값 유효성 검사를 포함합니다.")
    @PostMapping("/user/login")
    public String userLogin(@RequestBody RequestLoginDto requestLoginDto, @RequestParam int level, HttpServletRequest request, HttpServletResponse response) {
        User user = userService.login(requestLoginDto, level);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(1800); // 세션 만료 시간 30분 (1800초)

//            // 세션 ID를 쿠키로 설정
//            Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
//            sessionCookie.setHttpOnly(true);
//            sessionCookie.setSecure(true); // HTTPS를 사용할 경우
//            sessionCookie.setMaxAge(-1); // 브라우저가 닫힐 때 쿠키가 삭제되도록 설정
//            sessionCookie.setPath("/");
//
//            response.addCookie(sessionCookie);
            return "Login successful";
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "Invalid credentials";
        }
    }

    @ApiOperation(value = "회원가입", notes = "사용자의 회원가입을 합니다. 값 유효성 검사를 포함합니다.")
    @PostMapping("/user/signup")
    public String userSignUp(@RequestBody RequestLoginDto requestLoginDto) {
        userService.signUp(requestLoginDto);
        return "Signup completed successfully.";
    }

    @ApiOperation(value = "로그아웃", notes = "사용자를 로그아웃합니다.")
    @PostMapping("/user/logout")
    public String userLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "Logout successful";
    }


}
