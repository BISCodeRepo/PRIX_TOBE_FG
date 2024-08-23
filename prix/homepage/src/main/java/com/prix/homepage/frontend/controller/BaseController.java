package com.prix.homepage.frontend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
public class BaseController {
    public static final String SESSION_KEY_ID = "id";
    public static final String SESSION_KEY_NAME = "name";
    public static final String SESSION_KEY_LEVEL = "level";


    public static final int ANONY = 4;

    @ModelAttribute(SESSION_KEY_ID)
    public int id(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            //로그인이 되어있지 않을 경우 기존 코드와 동일하게 4(anony)반환
            return ANONY;
        }
        return (int)session.getAttribute(SESSION_KEY_ID);
    }

    @ModelAttribute(SESSION_KEY_NAME)
    public String name(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "anonymous";
        }
        return (String)session.getAttribute(SESSION_KEY_NAME);
    }

    @ModelAttribute(SESSION_KEY_LEVEL)
    public int level(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return 0;
        }
        return (int)session.getAttribute(SESSION_KEY_LEVEL);
    }
}
