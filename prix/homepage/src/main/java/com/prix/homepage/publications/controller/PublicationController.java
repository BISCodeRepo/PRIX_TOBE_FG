package com.prix.homepage.publications.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/publication")
public class PublicationController {

    @GetMapping
    public String publication(){
        return "publications";
    }
}
