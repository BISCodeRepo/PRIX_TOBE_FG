package com.prix.homepage.backend.history.controller;

import com.prix.homepage.backend.history.domain.SearchLog;
import com.prix.homepage.backend.history.service.HistoryService;
import com.prix.homepage.backend.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    /**
     * 특정 사용자의 히스토리 페이지를 반환합니다.
     *
     * @param session 현재 세션 객체.
     * @param model 뷰에 데이터를 전달하기 위한 모델 객체.
     * @return 히스토리 페이지 뷰 이름.
     */
    @GetMapping("/history")
    public String showHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");


        // 사용자가 없으면 로그인 페이지로 리디렉션합니다.
        if (user == null) {
            return "redirect:/login";
        }

        int userId = user.getUserid();

        List<SearchLog> searchLog = historyService.getHistoryByUserId(userId);

        model.addAttribute("historyList", searchLog);

        return "history.html";
    }
}