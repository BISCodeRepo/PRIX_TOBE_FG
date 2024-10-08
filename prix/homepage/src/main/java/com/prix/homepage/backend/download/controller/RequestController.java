package com.prix.homepage.backend.download.controller;

import com.prix.homepage.backend.basic.utils.Mailer;
import com.prix.homepage.backend.download.dto.RequestForm;
import com.prix.homepage.backend.download.mapper.RequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 *  소프트웨어 다운로드 요청 기능
 */
@Controller
@RequestMapping("/download/request")
public class RequestController {

    @Autowired
    private RequestMapper requestMapper;

    @Autowired
    private Mailer mailer;

    /**
     * 소프트웨어 요청 페이지
     * @param software 요청할 software
     * @param model
     * @return request.html 템플릿 반환
     */
    @GetMapping
    public String showRequestPage(@RequestParam(required = false) String software, Model model) {
        // software 파라미터가 없을 경우 기본값 설정
        if (software == null || software.equals("xxx")) {
            return "redirect:/download";
        }
        // 페이지에 소프트웨어 정보를 전달

        RequestForm requestForm = new RequestForm();
        requestForm.setAgreement("0xno");

        model.addAttribute("software", software);
        model.addAttribute("success", 0);  // 폼 초기 상태
        model.addAttribute("requestForm", requestForm); // 폼 초기 상태

        return "download/request";  // request.html 템플릿 반환
    }

    /**
     * 소프트웨어 요청 메일 전송
     * @param requestForm 요청 메일에 들어갈 내용
     * @param model
     * @return 요청 완료 페이지
     */
    @PostMapping
    public String processRequest(@ModelAttribute("requestForm") RequestForm requestForm, Model model) {

        String software = requestForm.getSoftware();
        String agreement = requestForm.getAgreement();
        String affliation = requestForm.getAffiliation();
        String title = requestForm.getTitle();
        String email = requestForm.getEmail();
        String name = requestForm.getName();
        String instrument = requestForm.getInstrument();

        model.addAttribute("software", software);
        model.addAttribute("email", email);


        if (software == null || software.equals("xxx")) {
            return "redirect:/download";
        }

        if (agreement.equals("1xyes") && email != null && !email.isEmpty()) {
            String subject = software + " software request from " + name;

            boolean mailSent = mailer.sendEmailToMe(subject, name, affliation, title, email, instrument);

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
