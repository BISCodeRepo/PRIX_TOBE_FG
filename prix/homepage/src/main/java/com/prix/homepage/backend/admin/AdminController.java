package com.prix.homepage.backend.admin;

import com.prix.homepage.backend.admin.entity.Database;
import com.prix.homepage.backend.admin.dto.UploadForm;
import com.prix.homepage.backend.admin.entity.Enzyme;
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

        Database [] database = adminService.selectAllFile();
        Enzyme[] enzyme = adminMapper.selectAllEnzyme();

        model.addAttribute("databases", database);
        model.addAttribute("enzymes", enzyme);

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
            Model model
            ) {

        int userId = (int)model.getAttribute(SESSION_KEY_ID);

        adminMapper.insertEnzyme(userId, enzyme.getName(), enzyme.getNtCleave(), enzyme.getCtCleave());
        return "redirect:/admin/configuration";
    }

}