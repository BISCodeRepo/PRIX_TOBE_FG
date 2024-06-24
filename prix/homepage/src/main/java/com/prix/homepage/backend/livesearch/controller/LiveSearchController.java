package com.prix.homepage.backend.livesearch.controller;

import com.prix.homepage.backend.livesearch.dto.UserSettingDto;
import com.prix.homepage.backend.livesearch.pojo.Modification;
import com.prix.homepage.backend.livesearch.service.ModificationService;
import com.prix.homepage.backend.livesearch.service.UserSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.prix.homepage.constants.prixConst.*;

@RequiredArgsConstructor
@Slf4j
@Controller
public class LiveSearchController {

    private final UserSettingService userSettingService;
    private final ModificationService modificationService;

    @GetMapping("/livesearch")
    public String liveSearch(){
        return "livesearch/livesearch";
    }

    @GetMapping("/modplus/search")
    public String modplus(Model model){
//        현재 로그인한 사용자의 ID 가져오기
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Integer id = userDetails.getId();
//        현재 로그인한 사용자의 이름 가져오기
//        String userName = userDetails.getUserName();
        Integer id = test; //테스트용 id 원래 id 는 anony(== 4)
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
//        현재 로그인한 사용자의 ID 가져오기
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Integer id = userDetails.getId();
        Integer id = test; //테스트용 id 원래 id 는 anony(== 4)

        model.addAttribute("id",id);
        model.addAttribute("var", var);
        model.addAttribute("engine", engine);
        model.addAttribute("sort", sort != null ? sort : "");
        List<Modification> modifications = Optional.ofNullable(modificationService.findModByUserAndCond(id, var == 1, engine == 1, sort))
                .orElse(new ArrayList<>());
        model.addAttribute("modifications", modifications);

        return "livesearch/var_ptms_list";
    }

    @GetMapping("/modplus/unimod_ptms_list")
    public String unimodPtmsList(
            @RequestParam(defaultValue = "1") Integer var,
            @RequestParam(defaultValue = "0") Integer engine,
            @RequestParam(defaultValue = "name asc") String sort,
            @RequestParam(defaultValue = "default") String filter,
            @RequestParam(value = "mod", required = false) List<String> modValues,
            Model model
            ){

        Integer id = test; //테스트용 id 원래 id 는 anony(== 4)
        boolean finished = false;
        ModFinder modFinder = new ModFinder(modValues);

        model.addAttribute("finished", finished);
        model.addAttribute("id", id);
        model.addAttribute("var", var);
        model.addAttribute("engine", engine);
        model.addAttribute("sortBy", sort);
        model.addAttribute("filter", filter);
        model.addAttribute("modFinder", modFinder);

        List<Modification> modifications = modificationService.findModifications(var==1,engine==1,sort,filter,id);

        model.addAttribute("modifications", modifications);

        return "livesearch/unimod_ptms_list";
    }

    @PostMapping("/modplus/unimod_ptms_list")
    public String unimodPtmsListPost(){


        return "livesearch/unimod_ptms_list";
    }

    class ModFinder {
        public ModFinder(List<String> values)
        {
            modValues = values;
        }
        public boolean findMod(String mod)
        {
            if (modValues == null)
                return false;
            for (int i = 0; i < modValues.size(); i++)
                if (mod.compareTo(modValues.get(i)) == 0)
                    return true;
            return false;
        }
        private List<String> modValues;
    }
}
