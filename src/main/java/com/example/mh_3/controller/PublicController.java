package com.example.mh_3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {

    @GetMapping("/about")
    public String about() {
        return "about-author";
    }
} 