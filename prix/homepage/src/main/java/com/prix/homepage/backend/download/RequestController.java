package com.prix.homepage.backend.download;

import com.prix.homepage.backend.download.RequestForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/download/request")
public class RequestController {

    @Autowired
    private RequestMapper requestMapper;

    @GetMapping
    public String showRequestPage(@RequestParam(required = false) String software, Model model) {
        // software 파라미터가 없을 경우 기본값 설정
        if (software == null || software.isEmpty()) {
            software = "xxx";
        }
        // 페이지에 소프트웨어 정보를 전달
        model.addAttribute("software", software);
        model.addAttribute("success", 0);  // 폼 초기 상태
        model.addAttribute("requestForm", new RequestForm()); // 폼 초기 상태

        return "download/request";  // request.html 템플릿 반환
    }

    @PostMapping
    public String processRequest(@ModelAttribute("requestForm") RequestForm requestForm, Model model) {

        String software = requestForm.getSoftware();
        String agreement = requestForm.getAgreement();
        String email = requestForm.getEmail();

//        if (software == null || software.equals("xxx")) {
//            return "redirect:/publications";
//        }

        if (agreement.equals("1xyes") && email != null && !email.isEmpty()) {
            String subject = software + " request from " + requestForm.getName();

            // Mail 전송 생략
            boolean mailSent = true;

            if (mailSent) {
                requestMapper.insertSoftwareRequest(
                        requestForm.getName(),
                        requestForm.getAffiliation(),
                        requestForm.getTitle(),
                        requestForm.getEmail(),
                        requestForm.getInstrument(),
                        requestForm.getSoftware()
                );
                model.addAttribute("success", 1);
            } else {
                model.addAttribute("success", 2);
            }
        } else {
            model.addAttribute("success", 0);
        }

        model.addAttribute("requestForm", requestForm);
        return "download/request";
    }
}
