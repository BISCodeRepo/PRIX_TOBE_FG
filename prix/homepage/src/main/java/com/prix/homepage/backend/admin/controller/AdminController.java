package com.prix.homepage.backend.admin.controller;

import com.prix.homepage.backend.admin.mapper.AdminMapper;
import com.prix.homepage.backend.admin.entity.*;
import com.prix.homepage.backend.account.domain.User;
import com.prix.homepage.backend.admin.service.AdminService;
import com.prix.homepage.frontend.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

import static com.prix.homepage.backend.basic.utils.PathUtil.*;

/**
 * ADMINISTRATION 기능
 * configuration, search log, users, request log 4개의 페이지 담당
 * admin으로 시작되는 url은 관리자 계정만 접근 가능. SessionLevelInterceptor에서 처리.
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController extends BaseController {

    private final AdminService adminService;
    private final AdminMapper adminMapper;

    @Autowired
    public AdminController(AdminService adminService, AdminMapper adminMapper) {
        this.adminService = adminService;
        this.adminMapper = adminMapper;
    }

    /**
     * 관리자 설정 페이지를 보여줌
     * @param model : 설정 페이지에 필요한 데이터를 전달하는 모델
     * @return 설정 페이지 뷰
     */
    @GetMapping("/configuration")
    public String configuration(Model model) {

        Database[] database = adminMapper.selectAllFile();
        Enzyme[] enzyme = adminMapper.selectAllEnzyme();
        SoftwareLog[] softwareLogs = adminMapper.selectAllSoftwareLogs();
        ModificationLog[] modificationLogs = adminMapper.selectAllModificationLogs();

        model.addAttribute("databases", database);
        model.addAttribute("enzymes", enzyme);
        model.addAttribute("softwareLogs", softwareLogs);
        model.addAttribute("modificationLogs", modificationLogs);

        model.addAttribute("modaMessage", adminMapper.getMessageBySoftware("mode"));
        model.addAttribute("dbondMessage", adminMapper.getMessageBySoftware("dbond"));
        model.addAttribute("nextsearchMessage", adminMapper.getMessageBySoftware("nextsearch"));
        model.addAttribute("signatureMessage", adminMapper.getMessageBySoftware("signature"));

        return "admin/configuration";
    }

    /**
     * 파일 업로드를 처리
     * @param dbName : 파일의 이름
     * @param dbFile : 업로드할 파일
     * @return 설정 페이지로 리다이렉트
     */
    @PostMapping("/upload")
    public String uploadDatabase(@RequestParam("db_name") String dbName,
                                 @RequestParam("db_file") MultipartFile dbFile) throws Exception {

        adminService.uploadDatabase(dbName, dbFile);
        return "redirect:/admin/configuration";
    }

    /**
     * 데이터베이스 파일 수정 및 삭제 처리
     * @param database : 데이터베이스 객체
     * @param action : 수정 또는 삭제 작업
     * @return 설정 페이지로 리다이렉트
     */
    @PostMapping("/update_file")
    public String updateFile(
            @ModelAttribute("Database") Database database,
            @RequestParam("action") String action) {

        if (" edit name ".equals(action)) {
            adminMapper.updateDatabase(database.getId(), database.getName());
        } else if (" unlink ".equals(action)) {
            adminMapper.deleteDatabase(database.getId());
        }
        return "redirect:/admin/configuration";
    }

    /**
     * enzyme 수정 및 삭제 처리
     * @param enzyme
     * @param action : 수정 또는 삭제 작업
     * @return 설정 페이지로 리다이렉트
     */
    @PostMapping("/update_enzyme")
    public String updateEnzymes(
            @ModelAttribute("Enzyme") Enzyme enzyme,
            @RequestParam("action") String action) {

        if (" edit name ".equals(action)) {
            adminMapper.updateEnzyme(enzyme.getId(), enzyme.getName(), enzyme.getNt_cleave(), enzyme.getCt_cleave());
        } else if (" delete ".equals(action)) {
            adminMapper.deleteEnzyme(enzyme.getId());
        }
        return "redirect:/admin/configuration";
    }

    /**
     * 새로운 enzyme 추가 처리
     * @param enzyme
     * @param model : 세션에서 사용자 정보를 가져오기 위한 모델
     * @return 설정 페이지로 리다이렉트
     */
    @PostMapping("/add_enzyme")
    public String addEnzyme(
            @ModelAttribute("Enzyme") Enzyme enzyme,
            Model model) {

        int userId = (int) model.getAttribute(SESSION_KEY_ID);
        adminMapper.insertEnzyme(userId, enzyme.getName(), enzyme.getNt_cleave(), enzyme.getCt_cleave());
        return "redirect:/admin/configuration";
    }

    /**
     * 수정된 PTM 정보와 XML 파일을 업로드하고 처리
     * @param modDate : 수정 날짜
     * @param modVersion : 수정 버전
     * @param file : 업로드할 파일
     * @return 설정 페이지로 리다이렉트
     */
    @PostMapping("/add_modification")
    public String addModification(@RequestParam("date") String modDate,
                                  @RequestParam("version") String modVersion,
                                  @RequestParam("file") MultipartFile file) throws Exception {

        adminService.addModification(modDate, modVersion, file);

        return "redirect:/admin/configuration";
    }


    /**
     * 소프트웨어 업로드 처리
     * @param sftwName : 소프트웨어 이름
     * @param sftwVersion : 소프트웨어 버전
     * @param sftwDate : 소프트웨어 배포 날짜
     * @param sftwFile : 업로드할 소프트웨어 파일
     * @return 설정 페이지로 리다이렉트
     */
    @PostMapping("/upload_software")
    public String handleSoftwareUpload(@RequestParam("sftw_name") String sftwName,
                                       @RequestParam("sftw_version") String sftwVersion,
                                       @RequestParam("sftw_date") String sftwDate,
                                       @RequestParam("sftw_file") MultipartFile sftwFile
                                       ) throws Exception {

        adminService.handleSoftwareUpload(sftwName, sftwVersion, sftwDate, sftwFile);

        return "redirect:/admin/configuration";  // 성공 시 리다이렉트
    }


    /**
     * 소프트웨어 메시지 수정 처리
     * @param modaMessage : MODa 소프트웨어 메시지
     * @param dbondMessage : DBond 소프트웨어 메시지
     * @param nextMessage : NextSearch 소프트웨어 메시지
     * @param signatureMessage : 서명 메시지
     * @return 설정 페이지로 리다이렉트
     */
    @PostMapping("/update_software_message")
    public String modifySoftwareMessages(
            @RequestParam(name = "modamsg", required = false) String modaMessage,
            @RequestParam(name = "dbondmsg", required = false) String dbondMessage,
            @RequestParam(name = "nextmsg", required = false) String nextMessage,
            @RequestParam(name = "signature", required = false) String signatureMessage
    ) {
        // 각 메시지를 이스케이프 처리한 후, 바로 Mapper를 통해 업데이트
        adminMapper.updateSoftwareMessage("mode", modaMessage.replace("'", "\\'"));
        adminMapper.updateSoftwareMessage("dbond", dbondMessage.replace("'", "\\'"));
        adminMapper.updateSoftwareMessage("nextsearch", nextMessage.replace("'", "\\'"));
        adminMapper.updateSoftwareMessage("signature", signatureMessage.replace("'", "\\'"));

        return "redirect:/admin/configuration";
    }

    /**
     * 검색 로그 조회
     * @param curPage : 현재 페이지
     * @param model : 검색 로그 데이터를 담는 모델
     * @return 검색 로그 페이지 뷰
     */
    @GetMapping("/searchlog")
    public String searchLog(@RequestParam(value = "p", defaultValue = "0") int curPage, Model model) {

        final int pageSize = 50;
        int offset = curPage * pageSize;

        List<SearchLog> searchLogs = adminMapper.findSearchLogs(offset, pageSize);
        int totalRecords = adminMapper.countSearchLogs();
        int totalPage = (totalRecords - 1) / pageSize + 1;

        model.addAttribute("searchLogs", searchLogs);
        model.addAttribute("curPage", curPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalRecords", totalRecords);
        model.addAttribute("totalPage", totalPage);

        return "admin/searchlog";
    }

    /**
     * 사용자 목록 조회
     * @param model : 사용자 목록 데이터를 담는 모델
     * @return 사용자 목록 페이지 뷰
     */
    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> users = adminMapper.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    /**
     * 사용자 권한 수정 또는 삭제 처리
     * @param upId : 권한 상향 대상 사용자 ID
     * @param downId : 권한 하향 대상 사용자 ID
     * @param delId : 삭제 대상 사용자 ID
     * @return 사용자 목록 페이지로 리다이렉트
     */
    @GetMapping("/update_user")
    public String modifyUser(
            @RequestParam(name = "up", required = false) Integer upId,
            @RequestParam(name = "down", required = false) Integer downId,
            @RequestParam(name = "del", required = false) Integer delId) {

        if (upId != null) {
            adminMapper.updateUserLevel(upId, 2);
        } else if (downId != null) {
            adminMapper.updateUserLevel(downId, 1);
        } else if (delId != null) {
            adminMapper.deleteUserById(delId);
        }
        return "redirect:/admin/users";
    }

    /**
     * 요청 로그 조회
     * @param model : 요청 로그 데이터를 담는 모델
     * @return 요청 로그 페이지 뷰
     */
    @GetMapping("/requestlog")
    public String showRequestLog(Model model) {
        List<SoftwareRequest> requests = adminMapper.findAllRequests();
        model.addAttribute("requests", requests);
        model.addAttribute("totalRequests", requests.size());
        return "admin/requestlog";
    }

    /**
     * 요청 수락 처리
     * @param id : 요청 ID
     * @param name : 요청한 사용자 이름
     * @param email : 요청한 사용자 이메일
     * @param software : 요청된 소프트웨어 이름
     * @return 요청 로그 페이지로 리다이렉트
     */
    @PostMapping("/requestlog/accept")
    public String updateRequest(
        @RequestParam(name = "id", required = false) Integer id,
        @RequestParam(name = "name", required = false) String name,
        @RequestParam(name = "email", required = false) String email,
        @RequestParam(name = "software", required = false) String software
        ) throws Exception {

        adminService.updateRequest(id, name, email, software);

        return "redirect:/admin/requestlog";
    }

    /**
     * 요청 삭제 처리
     * @param id : 삭제할 요청 ID
     * @return 요청 로그 페이지로 리다이렉트
     */
    @GetMapping("/requestlog/delete")
    public String deleteRequest(
            @RequestParam(name = "id", required = false) Integer id) {

        adminMapper.deleteRequestById(id);

        return "redirect:/admin/requestlog";
    }

    @ExceptionHandler(Exception.class)
    public String handleExceptions(Exception ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        model.addAttribute("stackTrace", sw.toString());
        return "admin/error";
    }

}
