package com.example.springuserdata.models;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Collection;

/**
 * Класс, отвечающий на хранение и проверку пользовательских данных.
 */
@Data
@Entity
@Table(name = "accounts")
public class User implements UserDetails {

    /**
     * Поле, хранящее Id пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    /**
     * Поле, хранящее логин пользователя.
     */
    @NotNull
    @Size(min = 4, message = "В логине должно быть не менее четырех символов")
    private String username;

    /**
     * Поле, хранящее пароль пользователя.
     */
    @NotNull
    @Size(min = 4, message = "В пароле должно быть не менее четырех символов")
    private String password = null;

    /**
     * Вспомогательное поле, хранящее "подтверждение пароля".
     */
    @NotNull
    @Size(min = 4, message = "В пароле должно быть не менее четырех символов")
    private String passwordRe = null;

    /**
     * Поле, хранящее Имя пользователя.
     */
    @NotBlank(message = "Поле не должно быть пустым")
    private String firstName;

    /**
     * Поле, хранящее Фамилию пользователя.
     */
    @NotBlank(message = "Поле не должно быть пустым")
    private String lastName;

    /**
     * Поле, хранящее пол пользователя.
     */
    @NotNull
    private String gender;

    /**
     * Поле, хранящее возраст пользователя.
     */
    @NotNull
    @Pattern(regexp = "1[8-9]|[2-9][0-9]|[1-9][0-9]\\d+", message = "Вы должны быть старше 18 лет")
    private String age;

    /**
     * Поле, хранящее серию паспорта пользователя.
     */
    @NotNull
    @Pattern(regexp = "\\d{4}", message = "Серия состоит из 4 цифр")
    private String passportSeries;

    /**
     * Поле, хранящее номер паспорта пользователя.
     */
    @NotNull
    @Pattern(regexp = "\\d{6}", message = "Номер состоит из 6 цифр")
    private String passportNumber;

    /**
     * Поле, хранящее номер телефона пользователя.
     */
    @NotNull
    @Pattern(regexp = "[7-8][0-9]{10}", message = "Номер телефона должен начинаться с 7 или 8")
    private String phoneNumber;

    /**
     * Поле, хранящее имя файла с личной информацией пользователя.
     */
    private String fileLink;

    /**
     * Поле, хранящее роль (права доступа) пользователя в приложении.
     */
    private String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
