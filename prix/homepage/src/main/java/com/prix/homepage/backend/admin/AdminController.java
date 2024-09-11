package com.prix.homepage.backend.admin;

import com.prix.homepage.backend.admin.entity.*;
import com.prix.homepage.backend.admin.dto.UploadForm;
import com.prix.homepage.backend.account.domain.User;
import com.prix.homepage.backend.basic.utils.Mailer;
import com.prix.homepage.frontend.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController extends BaseController {

    private final AdminService adminService;
    private final AdminMapper adminMapper;

    private final Mailer mailer;

    @Autowired
    public AdminController(AdminService adminService, AdminMapper adminMapper, Mailer mailer) {
        this.adminService = adminService;
        this.adminMapper = adminMapper;
        this.mailer = mailer;
    }

    @GetMapping("/configuration")
    public String configuration(Model model) {

        Database[] database = adminService.selectAllFile();
        Enzyme[] enzyme = adminMapper.selectAllEnzyme();
        SoftwareLog[] softwareLogs = adminMapper.selectAllSoftwareLogs();
        ModificationLog[] modificationLogs = adminMapper.selectAllModificationLogs();

        model.addAttribute("databases", database);
        model.addAttribute("enzymes", enzyme);
        model.addAttribute("softwareLogs", softwareLogs);
        model.addAttribute("modificationLogs", modificationLogs);

        // Optional: 메시지들 추가
        model.addAttribute("modaMessage", adminMapper.selectModaMessage());
        model.addAttribute("dbondMessage", adminMapper.selectDbondMessage());
        model.addAttribute("nextsearchMessage", adminMapper.selectNextSearchMessage());
        model.addAttribute("signatureMessage", adminMapper.selectSignatureMessage());

        return "admin/configuration";
    }

    @PostMapping("manage_file")
    public String manageFile(@ModelAttribute("UploadForm") UploadForm uploadForm) {
        adminService.uploadFile(uploadForm);
        return "redirect:/admin/configuration";
    }

    @PostMapping("/update_file")
    public String updateFile(
            @ModelAttribute("Database") Database database,
            @RequestParam("action") String action) {

        if (" edit name ".equals(action)) {
            adminMapper.updateDatabase(database.getId(), database.getName());
        } else if ("unlink".equals(action)) {
            adminMapper.deleteDatabase(database.getId());
        }
        return "redirect:/admin/configuration";
    }

    @PostMapping("/update_enzyme")
    public String updateEnzymes(
            @ModelAttribute("Enzyme") Enzyme enzyme,
            @RequestParam("action") String action) {

        if (" edit name ".equals(action)) {
            adminMapper.updateEnzyme(enzyme.getId(), enzyme.getName(), enzyme.getNtCleave(), enzyme.getCtCleave());
        } else if (" delete ".equals(action)) {
            adminMapper.deleteEnzyme(enzyme.getId());
        }
        return "redirect:/admin/configuration";
    }

    @PostMapping("/add_enzyme")
    public String addEnzyme(
            @ModelAttribute("Enzyme") Enzyme enzyme,
            Model model) {

        int userId = (int) model.getAttribute(SESSION_KEY_ID);
        adminMapper.insertEnzyme(userId, enzyme.getName(), enzyme.getNtCleave(), enzyme.getCtCleave());
        return "redirect:/admin/configuration";
    }

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


    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> users = adminMapper.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

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

    @GetMapping("/requestlog")
    public String showRequestLog(Model model) {
        List<SoftwareRequest> requests = adminMapper.findAllRequests();
        model.addAttribute("requests", requests);
        model.addAttribute("totalRequests", requests.size());
        return "admin/requestlog";
    }

    @PostMapping("/requestlog/accept")
    public String updateRequest(
        @RequestParam(name = "id", required = false) Integer id,
        @RequestParam(name = "name", required = false) String name,
        @RequestParam(name = "email", required = false) String email,
        @RequestParam(name = "software", required = false) String software
        ) {

        final String sendMsg = "hello";
        final String sig = "sig";
        final String path = "path";



        mailer.sendEmailToUser(name, email, software, sendMsg, sig, path);
        adminMapper.updateRequestState(id, 1); // 1 for accepted
        return "redirect:/admin/requestlog";
    }

    @GetMapping("/requestlog/delete")
    public String deleteRequest(
            @RequestParam(name = "id", required = false) Integer id) {


        adminMapper.deleteRequestById(id);

        return "redirect:/admin/requestlog";
    }




}
