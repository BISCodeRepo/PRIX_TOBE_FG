package com.prix.homepage.backend.history.controller;

import com.prix.homepage.backend.history.service.HistoryService;
import com.prix.homepage.frontend.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class HistoryController extends BaseController {

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

//        List<SearchLog> searchLog = historyService.getHistoryByUserId(userId);
//
//        model.addAttribute("historyList", searchLog);

        return "history.html";
    }
}