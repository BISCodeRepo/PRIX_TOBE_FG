package com.prix.homepage.backend.history.controller;

import com.prix.homepage.backend.admin.entity.SearchLog;
import com.prix.homepage.backend.history.mapper.HistoryMapper;
import com.prix.homepage.frontend.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class HistoryController extends BaseController {


    private final HistoryMapper historyMapper;

    @Autowired
    public HistoryController(HistoryMapper historyMapper) {
        this.historyMapper = historyMapper;
    }

    /**
     * 특정 사용자의 히스토리 페이지를 반환합니다.
     *
     * @param model 뷰에 데이터를 전달하기 위한 모델 객체.
     * @return 히스토리 페이지
     */

    @GetMapping("/searchlog")
    public String getHistory(Model model) {

        final int pageSize = 50;

//        int id = (int)model.getAttribute(SESSION_KEY_ID);
        int id = 778;

        List<SearchLog> searchLogs = historyMapper.findSearchLogs(id);

        model.addAttribute("searchLogs", searchLogs);
        model.addAttribute("pageSize", pageSize);

        return "livesearch/history";
    }
}