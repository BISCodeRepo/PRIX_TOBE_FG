package com.prix.homepage.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/download")
public class DownloadController extends BaseController{

    @GetMapping
    public String download(){
        return "download/download";
    }

    @GetMapping("/ACTG")
    public String actg_download() { return "download/actg"; }

    @GetMapping("/CIFTER")
    public String cifter_download() { return "download/cifter"; }

    @GetMapping("/DBond")
    public String dbond_download() { return "download/dbond"; }

    @GetMapping("/DDPSearch")
    public String ddp_download() { return "download/ddp_search"; }

    @GetMapping("/deMix")
    public String demix_download() { return "download/demix"; }


    @GetMapping("/MODa")
    public String moda_download() { return "download/mod_a"; }

    @GetMapping("/MODPlus")
    public String modplus_download() { return "download/mod_plus"; }


    @GetMapping("/MutCombinator")
    public String mutcombinator_download() { return "download/mut_combinator"; }

    @GetMapping("/NextSearch")
    public String nextsearch_download() { return "download/next_search"; }


    @GetMapping("/USE")
    public String use_download() { return "download/use"; }

}
