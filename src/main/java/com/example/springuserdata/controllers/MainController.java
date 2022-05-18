package com.example.springuserdata.controllers;

import com.example.springuserdata.models.SearchUser;
import com.example.springuserdata.models.User;
import com.example.springuserdata.service.AccountsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Slf4j
@RestController
@RequestMapping
public class MainController {

    @Autowired
    private AccountsService accountsService;

    @Value("${upload.path}")
    private String uploadPath;


    /* Главная страница */
    @GetMapping("/")
    public ModelAndView showHome() {
        return page("home.html");
    }


    /* Страница авторизации */
    @GetMapping("/login")
    public ModelAndView  showAuthorization() {
        return page("authorization.html");
    }


    /* Страница регистрации */
    @GetMapping("/registration")
    public ModelAndView showRegistration(@ModelAttribute("user") User user) {
        return page("registration.html");
    }

    @PostMapping("/registration")
    public ModelAndView processRegistration(@ModelAttribute("user") @Valid User user, BindingResult result,
                          @RequestParam("file") MultipartFile file, Model model) {
        String fileTypeError = null;
        if (!"application/pdf".equals(file.getContentType())) {
            fileTypeError = "fileTypeError";
            model.addAttribute(fileTypeError, "Файл должен быть с расширением '.pdf'");
        }
        if (accountsService.usernameCheck(user)) {
            result.rejectValue("username", "result.username", "Логин уже занят");
            return page("registration.html");
        }
        if (result.hasErrors() ||
                fileTypeError != null) {
            return page("registration.html");
        }
        if (!accountsService.passwordsMatch(user)) {
            result.rejectValue("passwordRe", "result.passwordRe", "Пароли не совпадают");
            return page("registration.html");
        }
        accountsService.saveAccount(user, file);
        return page("redirect:/login");
    }


    /* Страница поиска */
    @GetMapping("/search")
    public ModelAndView showSearch(@ModelAttribute("user") SearchUser searchUser, Model model) {
        model.addAttribute("accounts", accountsService.accounts(searchUser));
        return page("search.html");
    }

    @PostMapping("/search")
    public ModelAndView processSearch(@ModelAttribute("user") @Valid SearchUser searchUser, BindingResult result,
                          Model model, HttpServletResponse response) {
        model.addAttribute("accounts", accountsService.accounts(searchUser));
        return page("search.html");
    }


    /* Страница для скачивания файла с информацией о клиенте  */
    @PostMapping("/download")
    public ResponseEntity<InputStreamResource> fileView(@RequestParam("fileName") String filePath) throws FileNotFoundException {
        File file = new File(uploadPath + "/" + filePath);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(uploadPath + "/" + filePath));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName(filePath) + "\"")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .contentLength(file.length())
                .body(resource);
    }

    /* Возвращает исходное название файла */
    private String fileName(String str) {
        StringBuilder name = new StringBuilder(str);
        name = name.delete(0, name.indexOf(".") + 1);
        return name.toString();
    }


    /* Возвращает страницу */
    private ModelAndView page(String namePage) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(namePage);
        return modelAndView;
    }
}