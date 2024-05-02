package com.prix.homepage.user.controller.login;

import com.prix.homepage.domain.form.LoginForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @GetMapping("/user")
    public String loginUser(@ModelAttribute("loginForm") LoginForm loginForm){
        return "login";
    }

    @PostMapping("/user")
    public String loginUserAfter(@ModelAttribute("loginForm") LoginForm loginForm){

        if(loginForm.getUsername().equals("prix") && loginForm.getPassword().equals("prix")){
            log.info("login Success");
        }

        return "redirect:/";
    }

    @GetMapping("/admin")
    public String loginAgdmin(){
        return "admin_login";
    }

    @GetMapping("/agree")
    public String loginAgree(){
        return "agree";
    }

    @GetMapping("/register")
    public String loginRegister(){
        return "register";
    }
}
