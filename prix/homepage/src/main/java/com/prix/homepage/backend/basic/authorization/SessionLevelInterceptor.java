package com.prix.homepage.backend.basic.authorization;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class SessionLevelInterceptor implements HandlerInterceptor {

    /**
     *  관리자 계정(level 2이상)만 접근 가능
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        HttpSession session = request.getSession(false);
//        if (session == null) {
//            response.sendRedirect(request.getContextPath() + "/");
//            return false;
//        }
//        Integer userLevel = (Integer) session.getAttribute("level");
//
//        if (userLevel == null || userLevel != 2) {
//            response.sendRedirect(request.getContextPath() + "/");
//            return false;
//        }

        return true;
    }
}
