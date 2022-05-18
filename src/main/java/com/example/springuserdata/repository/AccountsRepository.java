package com.example.springuserdata.repository;

import com.example.springuserdata.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/* Взаимодействует с таблицей accounts */

@Component
public interface AccountsRepository extends CrudRepository<User, Long> {

    /* Находит нужный нам аккаунт по логину */
    User findByUsername(String username);

    /* Находит нужный нам аккаунт по паспорту */
    List<User> findByPassportSeriesAndPassportNumber(String passportSeries, String passportNumber);

    /* Находит все аккаунты */
    List<User> findAllBy();

    /* Удаляет аккаунт (для тестов) */
    void delete(User user);
}
