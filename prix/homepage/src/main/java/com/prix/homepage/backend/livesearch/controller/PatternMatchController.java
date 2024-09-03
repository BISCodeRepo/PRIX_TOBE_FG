package com.prix.homepage.backend.livesearch.controller;

import com.prix.homepage.backend.livesearch.service.patternmatch.PatternMatchService;
import com.prix.homepage.frontend.controller.BaseController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@Slf4j
@Controller
public class PatternMatchController extends BaseController {


//    @GetMapping("/livesearch/PatternMatch")
//    public String PatternMatch(){
//        return "livesearch/pattern_match.html";
//    }

    @Autowired
    private PatternMatchService patternMatchService;


    @GetMapping("/livesearch/PatternMatch/Result")
    public String showPatternMatchResult(
            @RequestParam("db_type") String dbType,
            @RequestParam("pattern1") String pattern1,
            @RequestParam(value = "pattern2", required = false) String pattern2,
            @RequestParam(value = "pattern3", required = false) String pattern3,
            @RequestParam(value = "pattern4", required = false) String pattern4,
            @RequestParam(value = "pattern5", required = false) String pattern5,
            @RequestParam(value = "format_type", required = false) String formatType,
            @RequestParam(value = "check_species", required = false) Boolean checkSpecies,
            @RequestParam(value = "species", required = false) String species,
            @RequestParam(value = "checkWithoutSq", required = false) Boolean checkWithoutSq,
            @RequestParam(value = "check_order", required = false) Boolean checkOrder,
            Model model) {

        String[] pattern = null;

        for(int i=0; i<1; i++){		// 한번만 실행되는 for 루프
            if (pattern2.equals("")) {
                pattern = new String[1];
                pattern[0] = pattern1;
                break;
            }

            else if (pattern3.equals("")) {
                pattern = new String[2];
                pattern[0] = pattern1;
                pattern[1] = pattern2;
                break;
            }

            else if (pattern4.equals("")) {
                pattern = new String[3];
                pattern[0] = pattern1;
                pattern[1] = pattern2;
                pattern[2] = pattern3;
                break;
            }

            else if (pattern5.equals("")) {
                pattern = new String[4];
                pattern[0] = pattern1;
                pattern[1] = pattern2;
                pattern[2] = pattern3;
                pattern[3] = pattern4;
                break;
            }

            else {
                pattern = new String[5];
                pattern[0] = pattern1;
                pattern[1] = pattern2;
                pattern[2] = pattern3;
                pattern[3] = pattern4;
                pattern[4] = pattern5;
                break;
            }
        }

        System.out.println(checkOrder);
        System.out.println(checkWithoutSq);
        System.out.println(checkOrder);
        System.out.println(species);

        patternMatchService.setParameter(formatType,dbType, pattern, checkSpecies, species, checkWithoutSq, checkOrder);

        String htmlOutput="";

        try {
            patternMatchService.MainMethod();
            htmlOutput = patternMatchService.getOneProtein();
            htmlOutput += patternMatchService.getParameter();

        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("proteinData", htmlOutput);



//        print("통과");
//        // DB에서 데이터 가져오기
//        UpdateTable updateTable = patternMatchService.getUpdateTable(dbType);
//        model.addAttribute("dbType", dbType.equals("1") ? "swiss_prot" : "genbank");
//        model.addAttribute("date", updateTable.getDate());

//        // 패턴 처리
//        String[] patterns = patternMatchService.getPatterns(pattern1, pattern2, pattern3, pattern4, pattern5);
//        model.addAttribute("patterns", patterns);

//        // 기타 옵션 처리
//        model.addAttribute("formatType", formatType);
//        model.addAttribute("checkSpecies", checkSpecies);
//        model.addAttribute("species", species);
//        model.addAttribute("checkExcept", checkExcept);
//        model.addAttribute("checkOrder", checkOrder);



        return "livesearch/pattern_match_result";  // patternMatchResult.html로 결과를 전달
    }

}

