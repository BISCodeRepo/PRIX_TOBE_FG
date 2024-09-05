package com.prix.homepage.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class HomeController extends BaseController {

    @GetMapping("/")
    public String home(){
        return "index";
    }

    @GetMapping("/contact")
    public String contact(){
        return "info/contact";
    }

    @GetMapping("/help")
    public String help(Model model) {
        // 행렬 데이터 생성
        List<List<Integer>> matrix = new ArrayList<>();

        matrix.add(new ArrayList<>(Arrays.asList(-28, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0)));
        matrix.add(new ArrayList<>(Arrays.asList(-17, 22, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 4, 0, 20, 0, 0, 0, 0, 0, 0)));
        matrix.add(new ArrayList<>(Arrays.asList(-1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0)));
        matrix.add(new ArrayList<>(Arrays.asList(1, 5, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0)));
        matrix.add(new ArrayList<>(Arrays.asList(4, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 0)));
        matrix.add(new ArrayList<>(Arrays.asList(12, 27, 0, 0, 3, 0, 0, 4, 0, 0, 2, 0, 0, 0, 12, 0, 0, 3, 2, 0, 8, 0)));
        matrix.add(new ArrayList<>(Arrays.asList(16, 0, 0, 13, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)));
        matrix.add(new ArrayList<>(Arrays.asList(30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0)));
        matrix.add(new ArrayList<>(Arrays.asList(43, 21, 4, 0, 2, 2, 0, 3, 0, 0, 2, 0, 0, 0, 0, 3, 0, 0, 4, 5, 0, 0)));
        matrix.add(new ArrayList<>(Arrays.asList(73, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 4, 0, 0, 0, 0)));
        matrix.add(new ArrayList<>(Arrays.asList(96, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 3, 0, 0, 0)));
        matrix.add(new ArrayList<>(Arrays.asList(166, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0)));

        // 모델에 데이터 추가
        model.addAttribute("matrix", matrix);

        // JSP로 이동
        return "info/help";
    }

    @GetMapping("/policy")
    public String policy(){
        return "policy";
    }

    @GetMapping("/test")
    public String test(){
        return "modplus";
    }

}
