package com.prix.homepage.frontend.controller;

import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class BaseController {
    public static final String USER_SESSION_KEY = "user";

    @ModelAttribute("isUserLoggedIn")
    public boolean isUserLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        return session != null && session.getAttribute(USER_SESSION_KEY) != null;
    }
}
