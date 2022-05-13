package com.example.springuserdata.controllers;

import com.example.springuserdata.models.SearchUser;
import com.example.springuserdata.repository.AccountsRepository;
import com.example.springuserdata.service.AccountsService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@Data
@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private AccountsRepository accountsRepository;

    @GetMapping
    public String show(@ModelAttribute("user") SearchUser searchUser, Model model) {
        model.addAttribute("accounts", accountsService.accounts(searchUser));
        return "search";
    }

    @PostMapping
    public String process(@ModelAttribute("user") @Valid SearchUser searchUser, BindingResult result,
                          Model model, HttpServletResponse response) {
        model.addAttribute("accounts", accountsService.accounts(searchUser));
        return "search";
    }
}
