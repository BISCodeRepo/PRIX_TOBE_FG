package com.prix.homepage.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/download")
public class DownloadController {

    @GetMapping
    public String download(){
        return "download/download";
    }

    @GetMapping("/actg")
    public String downloadActg() { return "download/actg"; }

    @GetMapping("/dbond")
    public String downloadDbond() { return "download/dbond"; }

}
