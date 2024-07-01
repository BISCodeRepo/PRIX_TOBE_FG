package com.prix.homepage.frontend.controller.login;

import com.prix.homepage.backend.user.domain.LoginForm;
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
    public String loginUserAfter(@ModelAttribute("loginForm") LoginForm loginForm, Model model, HttpServletRequest request) {
        RequestLoginDto requestLoginDto = new RequestLoginDto();
        requestLoginDto.setEmail(loginForm.getUsername());
        requestLoginDto.setPassword(loginForm.getPassword());

        User user = userService.login(requestLoginDto, 1);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(1800); // 세션 만료 시간 30분 (1800초)
            log.info("login Success");
            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login/login";
        }
    }

    @GetMapping("/admin")
    public String loginAdmin() {
        return "login/admin_login";
    }

    @GetMapping("/agree")
    public String loginAgree() {
        return "login/agree";
    }

    @GetMapping("/register")
    public String loginRegister() {
        return "login/register";
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
}
