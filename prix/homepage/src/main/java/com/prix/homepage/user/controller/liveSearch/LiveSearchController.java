package com.prix.homepage.user.controller.liveSearch;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/live-search")
public class LiveSearchController {

    @GetMapping
    public String liveSearch(){
        return "livesearch";
    }
}
