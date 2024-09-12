package com.prix.homepage.backend.account.controller;

import com.prix.homepage.backend.account.domain.LoginForm;
import com.prix.homepage.backend.account.domain.RegisterForm;
import com.prix.homepage.backend.account.domain.User;
import com.prix.homepage.backend.account.dto.RequestLoginDto;
import com.prix.homepage.backend.account.service.UserService;
import com.prix.homepage.frontend.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * 계정, 로그인 관련 기능 수행
 * 일반 유저, 관리자의 로그인 모두 수행
 */
@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController extends BaseController {

    public static final int SESSION_INACTIVATE = 1800; // 세션 만료 시간 30분 (1800초)
    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 사용자 로그인 페이지를 보여줌
     * @param loginForm : 로그인 폼 (사용자 입력을 받음)
     * @return String : 로그인 페이지 뷰
     */
    @GetMapping("/user")
    public String loginUser(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "login/login";
    }

    /**
     * 사용자 로그인 요청을 처리
     * @param loginForm : 사용자 로그인 폼 (아이디와 비밀번호 입력)
     * @param model : 뷰에 전달할 모델
     * @param request : 세션을 관리하기 위한 HTTP 요청 객체
     * @return String : 로그인 성공 시 메인 페이지로 리다이렉트, 실패 시 다시 로그인 페이지
     */
    @PostMapping("/user")
    public String loginUserPost(@ModelAttribute("loginForm") LoginForm loginForm, Model model, HttpServletRequest request) {
        RequestLoginDto requestLoginDto = new RequestLoginDto();
        requestLoginDto.setEmail(loginForm.getUsername());
        requestLoginDto.setPassword(loginForm.getPassword());

        User user = userService.login(requestLoginDto, 1);

        if (user != null) {
            // 세션에 계정 정보 등록
            HttpSession session = request.getSession();
            session.setAttribute(SESSION_KEY_ID, user.getId());
            session.setAttribute(SESSION_KEY_NAME, user.getName());
            session.setAttribute(SESSION_KEY_LEVEL, user.getLevel());
            session.setMaxInactiveInterval(SESSION_INACTIVATE); // 세션 만료 시간 설정 (30분)
            log.info("login Success");
            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login/login";
        }
    }

    /**
     * 관리자 로그인 페이지를 보여줌
     * @param loginForm : 로그인 폼 (관리자 입력을 받음)
     * @param model : 세션 정보를 담고 있는 모델 객체
     * @return String : 관리자 로그인 페이지 또는 관리 페이지로 리다이렉트
     */
    @GetMapping("/admin")
    public String loginAdmin(@ModelAttribute("loginForm") LoginForm loginForm, Model model) {

        int id = (int)model.getAttribute(SESSION_KEY_ID);
        int level = (int)model.getAttribute(SESSION_KEY_LEVEL);

        if (id != 4 && level >= 2) {
            return "redirect:/admin/configuration";
        }

        return "login/admin_login";
    }

    /**
     * 관리자 로그인 요청을 처리
     * @param loginForm : 관리자 로그인 폼 (아이디와 비밀번호 입력)
     * @param model : 뷰에 전달할 모델
     * @param request : 세션을 관리하기 위한 HTTP 요청 객체
     * @return String : 로그인 성공 시 관리자 페이지로 리다이렉트, 실패 시 다시 로그인 페이지
     */
    @PostMapping("/admin")
    public String loginAdminPost(@ModelAttribute("loginForm") LoginForm loginForm, Model model, HttpServletRequest request) {
        RequestLoginDto requestLoginDto = new RequestLoginDto();
        requestLoginDto.setEmail(loginForm.getUsername());
        requestLoginDto.setPassword(loginForm.getPassword());

        User user = userService.login(requestLoginDto, 2);

        if (user != null) {
            // 세션에 계정 정보 등록
            HttpSession session = request.getSession();
            session.setAttribute(SESSION_KEY_ID, user.getId());
            session.setAttribute(SESSION_KEY_NAME, user.getName());
            session.setAttribute(SESSION_KEY_LEVEL, user.getLevel());
            session.setMaxInactiveInterval(SESSION_INACTIVATE); // 세션 만료 시간 30분
            return "redirect:/admin/configuration";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login/admin_login";
        }
    }

    /**
     * 이용 약관 동의 페이지를 보여줌
     * @return String : 이용 약관 페이지 뷰
     */
    @GetMapping("/agree")
    public String loginAgree() {
        return "login/agree";
    }

    /**
     * 로그아웃 요청을 처리
     * @param request : 세션을 무효화하기 위한 HTTP 요청 객체
     * @return String : 로그아웃 후 메인 페이지로 리다이렉트
     */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 무효화
            log.info("logout Success");
        }
        return "redirect:/";
    }

    /**
     * 사용자 회원가입 페이지를 보여줌
     * @return String : 회원가입 페이지 뷰
     */
    @GetMapping("/register")
    public String loginRegister() {
        return "login/register";
    }

    /**
     * 사용자 회원가입 요청을 처리
     * @param registerForm : 회원가입 폼 (사용자 정보 입력)
     * @param model : 뷰에 전달할 모델
     * @param request : 세션을 관리하기 위한 HTTP 요청 객체
     * @return String : 회원가입 성공 시 메인 페이지로 리다이렉트, 실패 시 다시 회원가입 페이지
     */
    @PostMapping("/register")
    public String signUp(@ModelAttribute("loginForm") RegisterForm registerForm, Model model, HttpServletRequest request) {

        if (!registerForm.getPassword().equals(registerForm.getConfirmPassword())) {
            model.addAttribute("error", "Password mismatch.");
            return "login/register";
        }

        RequestLoginDto requestLoginDto = new RequestLoginDto();
        requestLoginDto.setEmail(registerForm.getUsername());
        requestLoginDto.setPassword(registerForm.getPassword());

        int result = userService.signUp(requestLoginDto);

        if(result == 1) {
            model.addAttribute("error", "Invalid username or password. The password must be at least 8 characters long.");
            return "login/register";
        }
        else if (result == 2) {
            model.addAttribute("error", "User ID already exists.");
            return "login/register";
        }
        else {
            // 유저 등록 성공
            User user = userService.login(requestLoginDto, 1);

            if (user != null) {
                // 세션에 계정 정보 등록
                HttpSession session = request.getSession();
                session.setAttribute(SESSION_KEY_ID, user.getId());
                session.setAttribute(SESSION_KEY_NAME, user.getName());
                session.setAttribute(SESSION_KEY_LEVEL, user.getLevel());
                session.setMaxInactiveInterval(SESSION_INACTIVATE); // 세션 만료 시간 설정 (30분)
                return "redirect:/";
            } else {
                model.addAttribute("error", "Invalid username or password");
                return "login/login";
            }
        }
    }

    /**
     * 계정 삭제 확인 페이지를 보여줌
     * @param request : 로그인 여부 확인을 위한 HTTP 요청 객체
     * @return String : 계정 삭제 확인 페이지 또는 로그인 안된 경우 메인 페이지로 리다이렉트
     */
    @GetMapping("/delete_account")
    public String deleteAccount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        // 로그인 안된 경우 메인 페이지로 보냄
        if (session == null) {
            return "redirect:/";
        }
        return "login/delete_account";
    }

    /**
     * 계정 삭제 요청을 처리
     * @param request : 세션을 무효화하고 계정을 삭제하기 위한 HTTP 요청 객체
     * @return String : 계정 삭제 후 메인 페이지로 리다이렉트
     */
    @PostMapping("/delete_account")
    public String deleteAccountPost(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        // 로그인 안된 경우 메인 페이지로 보냄
        if (session == null) {
            return "redirect:/";
        }

        int id = (int)session.getAttribute(SESSION_KEY_ID);

        userService.deleteUser(id);
        session.invalidate(); // 세션 무효화

        return "redirect:/";
    }
}
