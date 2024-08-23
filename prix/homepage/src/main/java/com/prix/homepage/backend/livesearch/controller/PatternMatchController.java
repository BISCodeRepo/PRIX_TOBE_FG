package com.prix.homepage.backend.livesearch.controller;

import com.prix.homepage.backend.livesearch.mapper.UpdateTableMapper;
import com.prix.homepage.backend.livesearch.pojo.UpdateTable;
import com.prix.homepage.backend.livesearch.service.patternmatch.PatternMatchService;
import com.prix.homepage.frontend.controller.BaseController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static jdk.nashorn.internal.objects.Global.print;

@RequiredArgsConstructor
@Slf4j
@Controller
public class PatternMatchController extends BaseController {

    @Autowired
    private UpdateTableMapper updateTableMapper;

    @GetMapping("/livesearch/PatternMatch")
    public String PatternMatch(Model model){

        UpdateTable swissProt = updateTableMapper.selectByDbName("swiss_prot");

        UpdateTable genBank = updateTableMapper.selectByDbName("genbank");

        model.addAttribute("swissProtDate", swissProt.getDate());
        model.addAttribute("genBankDate", genBank.getDate());


        return "livesearch/pattern_match";


    }

    @Autowired
    private PatternMatchService patternMatchService;


    @GetMapping("/livesearch/PatternMatch/Result")
    @ResponseBody
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
            @RequestParam(value = "check_except", required = false) Boolean checkExcept,
            @RequestParam(value = "check_order", required = false) Boolean checkOrder,
            Model model) {

        print("통과");
        // DB에서 데이터 가져오기
        UpdateTable updateTable = patternMatchService.getUpdateTable(dbType);
        model.addAttribute("dbType", dbType.equals("1") ? "swiss_prot" : "genbank");
        model.addAttribute("date", updateTable.getDate());

//        // 패턴 처리
//        String[] patterns = patternMatchService.getPatterns(pattern1, pattern2, pattern3, pattern4, pattern5);
//        model.addAttribute("patterns", patterns);

        // 기타 옵션 처리
        model.addAttribute("formatType", formatType);
        model.addAttribute("checkSpecies", checkSpecies);
        model.addAttribute("species", species);
        model.addAttribute("checkExcept", checkExcept);
        model.addAttribute("checkOrder", checkOrder);



        return "livesearch/pattern_match_result";  // patternMatchResult.html로 결과를 전달
    }

}
