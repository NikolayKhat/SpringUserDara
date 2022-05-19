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
import java.util.ArrayList;
import java.util.List;

/**
 * Класс содержит контроллеры, отвечающие за обработку запросов пользователя.
 */
@Slf4j
@RestController
@RequestMapping
public class MainController {

    /**
     * Сервисный класс для взаимодействия с аккаунтами клиентов.
     */
    @Autowired
    private AccountsService accountsService;

    /**
     * Поле содержит ссылку на место, где хранятся файлы с личной информацией пользователей.
     */
    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Метод переадресовывает на главную страницу.
     * @return домашнюю страницу.
     */
    @GetMapping("/")
    public ModelAndView showHome() {
        return page("home.html");
    }

    /**
     * Метод переадресовывает на страницу с авторизацией.
     * @return страницу с авторизацией.
     */
    @GetMapping("/login")
    public ModelAndView  showAuthorization() {
        return page("authorization.html");
    }

    /**
     * Метод переадресовывает на страницу с регистрацией.
     * @return страницу с регистрацией.
     */
    @GetMapping("/registration")
    public ModelAndView showRegistration(@ModelAttribute("user") User user) {
        return page("registration.html");
    }

    /**
     * Метод проверяет данные из формы с регистрацией.
     * <p>При успехе - переадресовывает на страницу с авторизацией.
     * <p>При неудаче - оставляет на той же странице.
     * @param user хранит и проверяет данные клиента.
     * @param result содержит все ошибки.
     * @param file содержит файл, который пользователь отправил.
     * @param model содержит все атрибуты.
     * @return нужную страницу.
     */
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

    /**
     * Метож переадресовывает на страницу с поиском клиента.
     * @param searchUser хранит данные о серии и номере паспорта.
     * @param model содержит все атрибуты.
     * @return страницу с поиском клиента.
     */
    @GetMapping("/search")
    public ModelAndView showSearch(@ModelAttribute("user") SearchUser searchUser, BindingResult result,
                                   Model model) {
        model.addAttribute("accounts", accountsService.accounts(searchUser, result));
        return page("search.html");
    }

    /**
     * Метод проверяет полученные данные.
     * @param searchUser содержит паспортные данные пользователя.
     * @param result содержит все ошибки.
     * @param model содержит все атрибуты.
     * @return страницу с поиском клиента.
     */
    @PostMapping("/search")
    public ModelAndView processSearch(@ModelAttribute("user") @Valid SearchUser searchUser, BindingResult result,
                          Model model) {
        model.addAttribute("accounts", accountsService.accounts(searchUser, result));
        return page("search.html");
    }

    /**
     * Метод предназначен для скачивания файла с информацией о клиенте.
     * @param filePath содержит имя файла, который нужно скачать.
     * @return файл, если все хорошо.
     * @throws FileNotFoundException
     */
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

    /**
     * Метод нужен для удаления из имени файла уникального UUID.
     * @param str содержит исходное имя файла из БД.
     * @return чисто имя файла.
     */
    private String fileName(String str) {
        StringBuilder name = new StringBuilder(str);
        name = name.delete(0, name.indexOf(".") + 1);
        return name.toString();
    }

    /**
     * Метод упрощает возврат нужной страницы.
     * @param namePage содержит имя страницы, к которой нужно перейти.
     * @return страницу.
     */
    private ModelAndView page(String namePage) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(namePage);
        return modelAndView;
    }
}