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


@RequiredArgsConstructor
@Slf4j
@Controller
public class ACTGController extends BaseController {

    private final UserService userService;

    private final ActgProcService actgService;

    private final ActgResultService actgResultService;

    private final PathUtil pathUtil;

    /**
     * GET 요청을 처리하여 ACTG 페이지를 반환하는 메서드.
     * 로그인한 사용자의 ID를 기반으로 사용자 이름을 가져와서 모델에 추가한다.
     * 만약 사용자가 로그인하지 않았다면 'anonymous'로 설정.
     */
    @GetMapping("/livesearch/ACTG")
    public String getACTG(Model model, HttpServletRequest request,
                          @LoginUserId Integer id) {

        String username = userService.getname(id);
        log.info("username ={}",username);
        if (username == null) {
            username = "anonymous";
        }
        model.addAttribute("username", username);

        return "livesearch/actg";
    }

    /**
     * GET 요청을 처리하여 ACTG 검색 프로세스를 처리하는 메서드.
     * 검색을 처리하고 결과가 완료되면 결과 페이지로 리다이렉트한다.
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
     * POST 요청을 처리하여 ACTG 검색 프로세스를 처리하는 메서드.
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
     * GET 요청을 처리하여 ACTG 결과 페이지를 반환하는 메서드.
     * 요청과 사용자 ID를 기반으로 결과 데이터를 처리하고 모델에 추가.
     */
    @GetMapping("/livesearch/ACTG/Result")
    public String ACTGResultPage(@LoginUserId Integer id,
                                 Model model,
                                 HttpServletRequest request,
                                 HttpSession session) {

        ActgResultDto actgResultDto = actgResultService.processResult(request,id);
        log.info("actgResultDto ={}",actgResultDto);
        String fileIndex = actgResultDto.getIndex();
        /* String resultFileDownloadPath = "C:/ACTG_db/ACTG_db/log/" + fileIndex + ".zip"; */
        String resultFileDownloadPath = "/livesearch/ACTG/Download?index=" + fileIndex;

        model.addAttribute("resultFileDownloadPath", resultFileDownloadPath);
        model.addAttribute("result", actgResultDto);
        return "livesearch/actg_result";
    }


    /**
     * 결과 페이지 .zip 파일을 다운로드하는 메서드.
     */
    @GetMapping("/livesearch/ACTG/Download")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@RequestParam("index") String index) throws FileNotFoundException {

        final String logDir = pathUtil.getGlobalDirectoryPath("/home/PRIX/ACTG_log/");

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
     */
    @GetMapping("/livesearch/ACTG/Help")
    public String showHelpPage() {
        return "livesearch/actg_help";
    }







}


