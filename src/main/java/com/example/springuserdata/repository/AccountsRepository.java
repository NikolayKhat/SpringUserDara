package com.example.springuserdata.repository;

import com.example.springuserdata.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Интерфейс, который взаимодействует с таблицей accounts.
 */
@Component
public interface AccountsRepository extends JpaRepository<User, Long> {

    /**
     * Метод находит нужный аккаунт по логину.
     * @param username логин пользователя.
     * @return информацию о пользователе.
     */
    User findByUsername(String username);

    /**
     * Метод находит всех (одного) пользователей с определенными паспортными данными.
     * @param passportSeries серия паспорта пользователя.
     * @param passportNumber номер паспорта пользователя.
     * @return список найденных пользователей.
     */
    List<User> findByPassportSeriesAndPassportNumber(String passportSeries, String passportNumber);

    /**
     * Метод находит всех пользователей.
     * @return список всех пользователей.
     */
    List<User> findAllBy();

    /**
     * Метод удаляет из таблицы accounts одного пользователя.
     * @param user хранит в себе данные пользователя, которого нужно удалять.
     */
    void delete(User user);
}
