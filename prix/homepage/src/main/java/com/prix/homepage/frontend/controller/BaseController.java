package com.prix.homepage.frontend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
public class BaseController {
    public static final String USER_SESSION_KEY = "user";
    public static final int ANONY = 4;

    @ModelAttribute("id")
    public int id(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            //로그인이 되어있지 않을 경우 기존 코드와 동일하게 4(anony)반환
            return ANONY;
        }
        return (int)session.getAttribute("id");
    }

    @ModelAttribute("name")
    public String name(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "anonymous";
        }
        return (String)session.getAttribute("name");
    }

    @ModelAttribute("level")
    public int level(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return 0;
        }
        return (int)session.getAttribute("level");
    }
}
