package com.prix.homepage.backend.livesearch.controller;

import com.prix.homepage.backend.account.argumentResolver.LoginUserId;
import com.prix.homepage.backend.account.domain.User;
import com.prix.homepage.backend.account.service.UserService;
import com.prix.homepage.backend.basic.utils.PathUtil;
import com.prix.homepage.backend.livesearch.dto.ActgDto;
import com.prix.homepage.backend.livesearch.dto.ActgResultDto;
import com.prix.homepage.backend.livesearch.service.ActgProcService;
import com.prix.homepage.backend.livesearch.service.ActgResultService;
import com.prix.homepage.frontend.controller.BaseController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import static com.prix.homepage.backend.basic.utils.PathUtil.PATH_ACTG_LOG;


@RequiredArgsConstructor
@Slf4j
@Controller
public class ACTGController extends BaseController {

    private final UserService userService;

    private final ActgProcService actgService;

    private final ActgResultService actgResultService;

    private final PathUtil pathUtil;

    /**
     * ACTG 페이지 요청을 처리하는 메서드.
     *
     * @param model - 페이지에 전달할 데이터를 담는 객체
     * @param request - 클라이언트 요청 정보
     * @param id - 로그인한 사용자의 ID
     * @return livesearch/actg 페이지로 이동
     */
    @GetMapping("/livesearch/ACTG")
    public String getACTG(Model model, HttpServletRequest request,
                          @LoginUserId Integer id) {

        String username = userService.getname(id);
        log.info("username ={}",username);
        if (username == null) {
            username = "anonymous";
        }
        model.addAttribute("username", username); // username form 에 반영

        return "livesearch/actg";
    }

    /**
     * GET 요청을 통해 ACTG 검색 프로세스를 처리하는 메서드.
     *
     * @param model - 페이지에 전달할 데이터를 담는 객체
     * @param id - 로그인한 사용자의 ID
     * @param request - 클라이언트 요청 정보
     * @param paramsMap - 각 요소 Map 형태로 전달
     * @return 검색 완료 시 결과 페이지로 리다이렉트, 그렇지 않으면 처리 페이지 반환
     */
    @GetMapping("/livesearch/ACTG/Process")
    public String postACTG(Model model,
                           @LoginUserId Integer id,
                           HttpServletRequest request, @RequestParam Map<String, String> paramsMap)
    {

        if(id == 4) return "redirect:/login/user?url=livesearch/ACTG";

        ActgDto result;
        log.info("ParamsMap Contents: ");
        paramsMap.forEach((key, value) -> {
            log.info(key + " : " + value);
        });
        try {
            result = actgService.process(id.toString(), request, paramsMap,null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("result = {}",result);
        if(result.getFinished()){
            return "redirect:/livesearch/ACTG/Result?index=" + result.getPrixIndex();
        }

        model.addAttribute("result",result);

        return "livesearch/actg_process";

    }


    /**
     * POST 요청을 통해 ACTG 검색 프로세스를 처리하는 메서드.
     *
     * @param model - 페이지에 전달할 데이터를 담는 객체
     * @param id - 로그인한 사용자의 ID
     * @param request - 클라이언트 요청 정보
     * @param paramsMap - 각 요소 Map 형태로 전달
     * @param peptideFile - 업로드된 peptide 파일
     * @param mutationFile - 업로드된 mutation 파일
     * @return 검색 완료 시 처리 페이지 반환
     */
    @PostMapping("/livesearch/ACTG/Process")
    public String postACTG(Model model,
                           @LoginUserId Integer id,
                           HttpServletRequest request, @RequestParam Map<String, String> paramsMap,
                           @RequestParam("peptideFile") MultipartFile peptideFile,
                           @RequestParam("mutationFile") MultipartFile mutationFile)
    {

        if(id == 4) return "redirect:/login/user?url=livesearch/ACTG";

        ActgDto result;
        log.info("ParamsMap Contents: ");
        paramsMap.forEach((key, value) -> {
            log.info(key + " : " + value);
        });
        try {
            result = actgService.process(id.toString(), request, paramsMap, new MultipartFile[]{peptideFile, mutationFile});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("result = {}",result);
        if(result.getFinished()){
            log.info("finish!");
        }
        model.addAttribute("result",result);

        return "livesearch/actg_process";

    }

    /**
     * ACTG 결과 페이지를 처리하는 메서드.
     *
     * @param id - 로그인한 사용자의 ID
     * @param model - 페이지에 전달할 데이터를 담는 객체
     * @param request - 클라이언트 요청 정보
     * @param session - 세션 정보
     * @return livesearch/actg_result 페이지로 이동
     */
    @GetMapping("/livesearch/ACTG/Result")
    public String ACTGResultPage(@LoginUserId Integer id,
                                 Model model,
                                 HttpServletRequest request,
                                 HttpSession session) {

        ActgResultDto actgResultDto = actgResultService.processResult(request,id);
        log.info("actgResultDto ={}",actgResultDto);
        String fileIndex = actgResultDto.getIndex();
        String resultFileDownloadPath = "/livesearch/ACTG/Download?index=" + fileIndex;

        model.addAttribute("resultFileDownloadPath", resultFileDownloadPath);
        model.addAttribute("result", actgResultDto);
        return "livesearch/actg_result";
    }


    /**
     * 결과 파일(.zip)을 다운로드하는 메서드.
     *
     * @param index - 다운로드할 파일의 인덱스
     * @return 파일을 다운로드 가능한 ResponseEntity 객체
     * @throws FileNotFoundException - 파일을 찾을 수 없을 때 예외 발생
     */
    @GetMapping("/livesearch/ACTG/Download")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@RequestParam("index") String index) throws FileNotFoundException {

        final String logDir = PATH_ACTG_LOG;

        String filePath = logDir + index + ".zip";
        File file = new File(filePath);

        if (!file.exists()) {
            return ResponseEntity.status(403).build();
        }

        InputStreamResource resource = null;
        try {
            resource = new InputStreamResource(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }


    /**
     * ACTG 도움말 페이지를 반환하는 메서드.
     *
     * @return livesearch/actg_help 페이지로 이동
     */
    @GetMapping("/livesearch/ACTG/Help")
    public String showHelpPage() {
        return "livesearch/actg_help";
    }







}


