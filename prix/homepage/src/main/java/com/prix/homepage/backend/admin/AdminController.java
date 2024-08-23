package com.prix.homepage.backend.admin;

import com.prix.homepage.backend.admin.dto.Database;
import com.prix.homepage.backend.admin.dto.UploadForm;
import com.prix.homepage.frontend.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/configuration")
    public String configuration(Model model) {
//        Database [] database  = new Database[]{Database.builder().id(1).name("asdf").file("asdf").build()};

        Database [] database = adminService.selectAllFile();

        model.addAttribute("databases", database);
        return "admin/configuration";
    }

    @PostMapping("manage_file")
    public String manageFile(@ModelAttribute("UploadForm") UploadForm uploadForm, Model model) {

        adminService.uploadFile(uploadForm);
        return "redirect:/admin/configuration";
    }


    @PostMapping("/update_file")
    public String updateFile(
            @ModelAttribute("Database") Database database,
            @RequestParam("action") String action) {

        // 버튼 클릭에 따른 처리
        if ("edit name".equals(action)) {
            adminMapper.updateDatabase(database.getId(), database.getName());
        } else if ("unlink".equals(action)) {
            adminMapper.deleteDatabase(database.getId());
        }
        return "redirect:/admin/configuration";
    }

}