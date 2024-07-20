package com.prix.homepage.frontend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
public class BaseController {
    public static final String USER_SESSION_KEY = "user";

    @ModelAttribute("isUserLoggedIn")
    public boolean isUserLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return false;
        }

        Object userIdObj = session.getAttribute(USER_SESSION_KEY);
        if (userIdObj == null) {
            return false;
        }
        Integer userId = (Integer)userIdObj;
        return userId != null && !userId.equals(4);
    }
}
