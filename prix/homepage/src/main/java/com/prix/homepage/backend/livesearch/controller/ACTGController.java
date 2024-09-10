package com.prix.homepage.backend.livesearch.controller;

import com.prix.homepage.backend.account.argumentResolver.LoginUserId;
import com.prix.homepage.backend.account.domain.User;
import com.prix.homepage.backend.account.service.UserService;
import com.prix.homepage.backend.livesearch.dto.ActgDto;
import com.prix.homepage.backend.livesearch.dto.ActgResultDto;
import com.prix.homepage.backend.livesearch.service.ActgProcService;
import com.prix.homepage.backend.livesearch.service.ActgResultService;
import com.prix.homepage.frontend.controller.BaseController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
import java.io.IOException;
import java.util.Map;


@RequiredArgsConstructor
@Slf4j
@Controller
public class ACTGController extends BaseController {

    private final UserService userService;

    private final ActgProcService actgService;

    private final ActgResultService actgResultService;
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

    @GetMapping("/livesearch/ACTG/Result")
    public String ACTGResultPage(@LoginUserId Integer id,
                                 Model model,
                                 HttpServletRequest request,
                                 HttpSession session) {

        ActgResultDto actgResultDto = actgResultService.processResult(request,id);
        log.info("actgResultDto ={}",actgResultDto);
        String fileIndex = actgResultDto.getIndex();
        /* String resultFileDownloadPath = "C:/ACTG_db/ACTG_db/log/" + fileIndex + ".zip"; */
        String resultFileDownloadPath = "/ACTG/download?index=" + fileIndex;

        model.addAttribute("resultFileDownloadPath", resultFileDownloadPath);
        model.addAttribute("result", actgResultDto);
        return "livesearch/actg_result";
    }



    @GetMapping("/ACTG/download")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@RequestParam("index") String index) {
        // Construct the file path
        String filePath = "C:/ACTG_db/ACTG_db/log/" + index + ".zip";
        File file = new File(filePath);

        // Check if the file exists
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Create a resource and set the headers for download
        Resource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }



}


