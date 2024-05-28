package com.prix.homepage.backend.livesearch.controller;

import com.prix.homepage.backend.livesearch.service.UserModificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/livesearch")
public class ApiDataController {

    private final UserModificationService userModificationService;

    @GetMapping("/user-modification/count")
    public Integer getUserModificationCount(@RequestParam Integer userId, @RequestParam Integer variable, @RequestParam Integer engine) {
        return userModificationService.getUserModificationCount(userId, variable == 1, engine == 1);
    }
}
