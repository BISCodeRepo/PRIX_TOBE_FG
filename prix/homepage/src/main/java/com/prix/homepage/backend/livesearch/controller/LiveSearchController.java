package com.prix.homepage.backend.livesearch.controller;

import com.prix.homepage.backend.livesearch.dto.UserSettingDto;
import com.prix.homepage.backend.livesearch.service.UserSettingService;
import com.prix.homepage.constants.prixConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.prix.homepage.constants.prixConst.*;

@RequiredArgsConstructor
@Slf4j
@Controller
public class LiveSearchController {

    private final UserSettingService userSettingService;

    @GetMapping("/livesearch")
    public String liveSearch(){
        return "livesearch";
    }

    @GetMapping("/modplus/search")
    public String modplus(Model model){
//        현재 로그인한 사용자의 ID 가져오기
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Integer id = userDetails.getId();
//        현재 로그인한 사용자의 이름 가져오기
//        String userName = userDetails.getUserName();
        Integer id = anony;
        String userName = "anonymous";
        model.addAttribute("userName",userName);
        model.addAttribute("id",id);

        UserSettingDto userSetting = userSettingService.findUserSettingById(id);
        model.addAttribute("userSetting",userSetting);


        return "livesearch/modplus";
    }

    @GetMapping("/modplus/var_ptms_list")
    public String varPtmsList(
            @RequestParam(defaultValue = "1") Integer var,
            @RequestParam(defaultValue = "0") Integer engine,
            @RequestParam(required = false) String sort,
            Model model) {

        model.addAttribute("var", var);
        model.addAttribute("engine", engine);
        model.addAttribute("sort", sort != null ? sort : "");

        return "livesearch/var_ptms_list";
    }
}
