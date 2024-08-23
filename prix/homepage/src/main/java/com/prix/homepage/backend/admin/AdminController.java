package com.prix.homepage.backend.admin;

import com.prix.homepage.backend.admin.dto.Database;
import com.prix.homepage.backend.admin.dto.UploadForm;
import com.prix.homepage.frontend.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController extends BaseController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/configuration")
    public String configuration(Model model) {
        Database [] database  = new Database[]{Database.builder().id(1).name("asdf").file("asdf").build()};
        model.addAttribute("databases", database);
        return "admin/configuration";
    }

    @PostMapping("manage_file")
    public String manageFile(@ModelAttribute("UploadForm") UploadForm uploadForm, Model model) {

        adminService.uploadFile(uploadForm);
        return "admin/configuration";
    }
}