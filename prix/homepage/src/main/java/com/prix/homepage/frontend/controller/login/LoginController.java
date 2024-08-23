package com.prix.homepage.frontend.controller.login;

import com.prix.homepage.backend.user.domain.LoginForm;
import com.prix.homepage.backend.user.domain.RegisterForm;
import com.prix.homepage.backend.user.domain.User;
import com.prix.homepage.backend.user.dto.RequestLoginDto;
import com.prix.homepage.backend.user.service.UserService;
import com.prix.homepage.frontend.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController extends BaseController {

    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String loginUser(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "login/login";
    }

    @PostMapping("/user")
    public String loginUserPost(@ModelAttribute("loginForm") LoginForm loginForm, Model model, HttpServletRequest request) {
        RequestLoginDto requestLoginDto = new RequestLoginDto();
        requestLoginDto.setEmail(loginForm.getUsername());
        requestLoginDto.setPassword(loginForm.getPassword());

        User user = userService.login(requestLoginDto, 1);

        if (user != null) {
            //세션에 계정 정보 등록
            HttpSession session = request.getSession();
            session.setAttribute(SESSION_KEY_ID, user.getId());
            session.setAttribute(SESSION_KEY_NAME, user.getName());
            session.setAttribute(SESSION_KEY_LEVEL, user.getLevel());
            session.setMaxInactiveInterval(1800); // 세션 만료 시간 30분 (1800초)
            log.info("login Success");
            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login/login";
        }
    }

    @GetMapping("/admin")
    public String loginAdmin(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "login/admin_login";
    }

    @PostMapping("/admin")
    public String loginAdminPost(@ModelAttribute("loginForm") LoginForm loginForm, Model model, HttpServletRequest request) {
        RequestLoginDto requestLoginDto = new RequestLoginDto();
        requestLoginDto.setEmail(loginForm.getUsername());
        requestLoginDto.setPassword(loginForm.getPassword());

        User user = userService.login(requestLoginDto, 2);

        if (user != null) {
            //세션에 계정 정보 등록
            HttpSession session = request.getSession();
            session.setAttribute(SESSION_KEY_ID, user.getId());
            session.setAttribute(SESSION_KEY_NAME, user.getName());
            session.setAttribute(SESSION_KEY_LEVEL, user.getLevel());
            session.setMaxInactiveInterval(1800); // 세션 만료 시간 30분 (1800초)
            log.info("login Success");
            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login/admin_login";
        }
    }

    @GetMapping("/agree")
    public String loginAgree() {
        return "login/agree";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            log.info("logout Success");
        }
        return "redirect:/";
    }

    @GetMapping("/register")
    public String loginRegister() {
        return "login/register";
    }

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

        log.info(Integer.toString(result));

        if(result == 1) {
            model.addAttribute("error", "Invalid username or password.");
            return "login/register";
        }
        else if (result == 2) {
            model.addAttribute("error", "User ID already exists.");
            return "login/register";
        }
        else {
            //유저 등록 성공
            return "redirect:/";
        }
    }

    @GetMapping("/delete_account")
    public String deleteAccount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        //로그인 안된 경우 메인 페이지로 보냄
        if (session == null) {
            return "redirect:/";
        }
        return "login/delete_account";
    }

    @PostMapping("/delete_account")
    public String deleteAccountPost(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        //로그인 안된 경우 메인 페이지로 보냄
        if (session == null) {
            return "redirect:/";
        }

        int id = (int)session.getAttribute(SESSION_KEY_ID);

        userService.deleteUser(id);
        session.invalidate();

        return "redirect:/";
    }


}
