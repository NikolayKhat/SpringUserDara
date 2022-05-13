package com.example.springuserdata.controllers;

import com.example.springuserdata.models.User;
import com.example.springuserdata.service.AccountsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/registration")
public class RegistrationController {

    @Autowired
    private AccountsService accountsService;

    @GetMapping
    public String show(@ModelAttribute("user") User user) {
        return "registration";
    }

    @PostMapping
    public String process(@ModelAttribute("user") @Valid User user, BindingResult result,
                          @RequestParam("file") MultipartFile file,
                          Model model) {
        String fileTypeError = null;
        if (!"application/pdf".equals(file.getContentType())) {
            fileTypeError = "fileTypeError";
            model.addAttribute(fileTypeError, "Файл должен быть с расширением '.pdf'");
        }
        if (accountsService.usernameCheck(user)) {
            result.rejectValue("username", "result.username", "Логин уже занят");
            return "registration";
        }
        if (result.hasErrors() ||
            fileTypeError != null) {
            return "registration";
        }
        if (!accountsService.passwordsMatch(user)) {
            result.rejectValue("passwordRe", "result.passwordRe", "Пароли не совпадают");
            return "registration";
        }
        accountsService.saveAccount(user, file);
        return "redirect:/login";
    }
}
