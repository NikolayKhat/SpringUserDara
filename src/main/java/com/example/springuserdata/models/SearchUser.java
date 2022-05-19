package com.example.springuserdata.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Вспомогательный класс, отвечающий за хранение паспортных данных для поиска пользователя.
 */
@Data
@RequiredArgsConstructor
public class SearchUser {

    /**
     * Поле, хранящие в себе серию паспорта.
     */
    @NotNull
    @Size(min = 4, max = 4, message = "В серии паспорта должно быть 4 цифры")
    private String passportSeries;

    /**
     * Поле, хранящие в номер паспорта.
     */
    @NotNull
    @Size(min = 6, max = 6, message = "В номере паспорта должно быть 6 цифры")
    private String passportNumber;
}
