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


    @Autowired
    private PatternMatchService patternMatchService;

    /**
     * 패턴 매칭 결과 페이지를 보여주는 메서드.
     *
     * @param dbType - 사용자가 선택한 데이터베이스 유형
     * @param pattern1 - 첫 번째 패턴
     * @param pattern2 - 두 번째 패턴 (필수 아님)
     * @param pattern3 - 세 번째 패턴 (필수 아님)
     * @param pattern4 - 네 번째 패턴 (필수 아님)
     * @param pattern5 - 다섯 번째 패턴 (필수 아님)
     * @param formatType - 출력 형식 유형
     * @param checkSpecies - 종 확인 여부 (기본값: false)
     * @param species - 사용자가 선택한 종 (필수 아님)
     * @param checkWithoutSq - 서열 정보 없이 조회할지 여부 (기본값: false)
     * @param checkOrder - 패턴의 순서를 유지할지 여부 (기본값: false)
     * @param model - 뷰로 전달할 데이터를 담는 객체
     * @return "livesearch/pattern_match_result" 페이지로 이동
     */
    @GetMapping("/livesearch/PatternMatch/Result")
    public String showPatternMatchResult(
            @RequestParam("db_type") String dbType,
            @RequestParam("pattern1") String pattern1,
            @RequestParam(value = "pattern2", required = false) String pattern2,
            @RequestParam(value = "pattern3", required = false) String pattern3,
            @RequestParam(value = "pattern4", required = false) String pattern4,
            @RequestParam(value = "pattern5", required = false) String pattern5,
            @RequestParam("format_type") String formatType,
            @RequestParam(value = "check_species", defaultValue = "0") Boolean checkSpecies,
            @RequestParam(value = "species", required = false) String species,
            @RequestParam(value = "checkWithoutSq", defaultValue = "0") Boolean checkWithoutSq,
            @RequestParam(value = "check_order", defaultValue = "0") Boolean checkOrder,
            Model model) {

        String[] pattern = null;
        // 패턴 배열 생성. 각 패턴이 비어 있는지 확인하여 배열 크기 결정
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

        // 패턴 매칭 서비스를 위한 파라미터 설정
        patternMatchService.setParameter(formatType,dbType, pattern, checkSpecies, species, checkWithoutSq, checkOrder);

        String htmlOutput="";

        try {
            // main method 를 통해 HTML 값을 string 형태로 출력
            patternMatchService.MainMethod();
            htmlOutput = patternMatchService.generateNextBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("proteinData", htmlOutput);



        return "livesearch/pattern_match_result";  // patternMatchResult.html로 결과를 전달
    }

    /**
     * 더 많은 단백질 데이터를 비동기적으로 로드하는 메서드.
     *
     * @return 추가된 단백질 데이터를 HTML 형식으로 반환
     */
    @GetMapping("/loadMoreProteinData")
    @ResponseBody
    public String loadMoreProteinData() {
        String htmlOutput = "";

        try {
            htmlOutput = patternMatchService.generateNextBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return htmlOutput;
    }

}

