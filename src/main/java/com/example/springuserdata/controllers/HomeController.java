package com.example.springuserdata.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/")
public class HomeController {

    /* Возвращает на главную страницу */
    @GetMapping
    public String home(Model model) {
        return "home";
    }
}
