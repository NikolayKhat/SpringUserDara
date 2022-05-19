package com.example.springuserdata.service;

import com.example.springuserdata.models.SearchUser;
import com.example.springuserdata.models.User;
import com.example.springuserdata.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Сервисный класс, отвечающий за чтение и записи данных клиента в таблицу accounts.
 */
@Service
public class AccountsService implements UserDetailsService {

    /**
     * Поле отвечает за доступ к данным из таблицы accounts.
     */
    @Autowired
    AccountsRepository accountsRepository;

    /**
     * Поле содержит ссылку на место, где хранятся файлы с личной информацией пользователей.
     */
    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Метод проверяет, не занят ли логин.
     * @param user хранит и проверяет данные пользователя.
     * @return true/false, если пользователь существует/не существует.
     */
    @Transactional(readOnly = true)
    public boolean usernameCheck(User user) {
        User username = accountsRepository.findByUsername(user.getUsername());
        if (username != null) return  true;
        else return false;
    }

    /**
     * Метод проверяет, совпадают ли пароли.
     * @param user хранит и проверяет данные пользователя.
     * @return true/false, если пароли совпадают/не совпадают.
     */
    public boolean passwordsMatch(User user) {
        if (user.getPassword().equals(user.getPasswordRe())) return true;
        else return false;
    }

    /**
     * Метод сохраняет дынные пользователя в БД accounts.
     * @param user хранит и проверяет данные пользователя.
     * @param file хранить файл с личной информацией пользователя.
     */
    @Transactional
    public void saveAccount(User user, MultipartFile file) {
        saveFile(user, file);
        user.setRole("USER");
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setPasswordRe(new BCryptPasswordEncoder().encode(user.getPasswordRe()));
        accountsRepository.save(user);
    }

    /**
     * Метод сохраняет файл в uploadPath и передает название в user.
     * @param user хранит и проверяет данные пользователя.
     * @param file хранит файл с личной информацией пользователя.
     */
    public void saveFile(User user, MultipartFile file) {
        if (file != null) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuiFile = UUID.randomUUID().toString();
            String resultFileName = uuiFile + "." + file.getOriginalFilename();
            try {
                file.transferTo(new File(uploadPath + "/" + resultFileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            user.setFileLink(resultFileName);
        }

    }

    /**
     * Метод ищет нужные аккаунты пользователей.
     * @param searchUser содержит паспортные данные пользователя.
     * @param result содержит все ошибки.
     * @return список клиентов.
     */
    @Transactional(readOnly = true)
    public List<User> accounts(SearchUser searchUser, BindingResult result) {
        String series = searchUser.getPassportSeries();
        String number = searchUser.getPassportNumber();
        if (series == null || number == null ||
            series.equals("") || number.equals("")) {
            return accountsRepository.findAllBy();
        }
        if (!accountsRepository.findByPassportSeriesAndPassportNumber(searchUser.getPassportSeries(), searchUser.getPassportNumber()).isEmpty()) {
            return accountsRepository.findByPassportSeriesAndPassportNumber(series, number);
        } else {
            result.rejectValue("passportSeries", "result.passportSeries", "Пользователя с такими паспортными данными не существует");
            result.rejectValue("passportNumber", "result.passportNumber", "");
            return accountsRepository.findAllBy();
        }
    }

    /**
     * Метод нужен для реализации интерфейса UserDetailsService.
     * @param username содержит логин пользователя.
     * @return пользователя с заданным логином.
     * @throws UsernameNotFoundException
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = accountsRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        return user;
    }
}
