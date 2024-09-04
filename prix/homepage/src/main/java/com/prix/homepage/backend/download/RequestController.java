package com.prix.homepage.backend.download;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/download/request")
public class RequestController {

    @Autowired
    private RequestMapper requestMapper;

//    @Autowired
//    private Mailer mailer;

    @GetMapping("/")
    public String request() {
        return "";
    }

    @GetMapping
    public String showRequestPage(@RequestParam(required = false) String software, Model model) {
        // software 파라미터가 없을 경우 기본값 설정
        if (software == null || software.isEmpty()) {
            software = "xxx";
        }
        // 페이지에 소프트웨어 정보를 전달
        model.addAttribute("software", software);
        model.addAttribute("success", 0);  // 폼 초기 상태

        return "download/request";  // request.html 템플릿 반환
    }

    @PostMapping("/")
    public String processRequest(
            @RequestParam String software,
            @RequestParam String agreement,
            @RequestParam String name,
            @RequestParam String affiliation,
            @RequestParam String title,
            @RequestParam String email,
            @RequestParam String instrument,
            Model model) {

        if (software == null || software.equals("xxx")) {
            return "redirect:/publications";
        }

        if (agreement.equals("1xyes") && email != null && !email.isEmpty()) {
            String subject = software + " request from " + name;
//            boolean mailSent = mailer.sendEmailToMe(subject, name, affiliation, title, email, instrument);
            boolean mailSent = true;

            if (mailSent) {
                // Mapper를 통해 SQL 처리
                requestMapper.insertSoftwareRequest(name, affiliation, title, email, instrument, software);
                model.addAttribute("success", 1);
            } else {
                model.addAttribute("success", 2);
            }
        } else {
            model.addAttribute("success", 0);
        }

        model.addAttribute("software", software);
        return "download/request";
    }
}
