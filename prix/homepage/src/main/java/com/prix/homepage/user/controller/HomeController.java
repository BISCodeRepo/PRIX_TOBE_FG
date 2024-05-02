package com.prix.homepage.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(){
        return "index";
    }

    @GetMapping("/contact")
    public String contact(){
        return "contact";
    }

    @GetMapping("/help")
    public String help(){
        return "help";
    }

    @GetMapping("/policy")
    public String policy(){
        return "help";
    }

}
