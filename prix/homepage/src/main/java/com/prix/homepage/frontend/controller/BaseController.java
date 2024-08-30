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
            // 로그인이 되어있지 않을 경우 4(anony) 반환
            return ANONY;
        }
        Integer sessionId = (Integer) session.getAttribute(SESSION_KEY_ID);
        if (sessionId == null) {
            // 세션에 ID가 없을 경우에도 ANONY 반환
            log.warn("Session ID is null, returning ANONY");
            return ANONY;
        }
        return sessionId;
    }

    @ModelAttribute(SESSION_KEY_NAME)
    public String name(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "anonymous";
        }
        String sessionName = (String) session.getAttribute(SESSION_KEY_NAME);
        if (sessionName == null) {
            log.warn("Session NAME is null, returning 'anonymous'");
            return "anonymous";
        }
        return sessionName;
    }

    @ModelAttribute(SESSION_KEY_LEVEL)
    public int level(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return 0;
        }
        Integer sessionLevel = (Integer) session.getAttribute(SESSION_KEY_LEVEL);
        if (sessionLevel == null) {
            log.warn("Session LEVEL is null, returning 0");
            return 0;
        }
        return sessionLevel;
    }
}
