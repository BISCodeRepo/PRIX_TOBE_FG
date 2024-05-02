package com.prix.homepage.backend.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class ApiLoginController {
    @GetMapping("/test")
    public String test() {
        return "Hello this is test";
    }
}
