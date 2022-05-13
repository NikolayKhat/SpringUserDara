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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/* Сервис для считывания и добавления в таблицу accounts клиента*/
@Service
public class AccountsService implements UserDetailsService {

    @Autowired
    AccountsRepository accountsRepository;

    @Value("${upload.path}")
    private String uploadPath;

    /* Проверяет, не занят ли логин */
    public boolean usernameCheck(User user) {
        User username = accountsRepository.findByUsername(user.getUsername());
        if (username == null) return false;
        else return true;
    }

    /* Проверяет, совпадают ли пароли */
    public boolean passwordsMatch(User user) {
        if (user.getPassword().equals(user.getPasswordRe())) return true;
        else return false;
    }

    /* Сохраняет аккаунт в accounts */
    public void saveAccount(User user, MultipartFile file) {
        saveFile(user, file);
        user.setRole("USER");
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setPasswordRe(new BCryptPasswordEncoder().encode(user.getPasswordRe()));
        accountsRepository.save(user);
    }

    /* Сохранение файла в папке uploads */
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

    /* Возвращает список аккаунтов */
    public List<User> accounts(SearchUser searchUser) {
        String series = searchUser.getPassportSeries();
        String number = searchUser.getPassportNumber();
        if (series == null || number == null ||
            series.equals("") || number.equals("")) {
            return accountsRepository.findAllBy();
        } else {
            return accountsRepository.findByPassportSeriesAndPassportNumber(series, number);
        }
    }

    /* security */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = accountsRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        return user;
    }
}
