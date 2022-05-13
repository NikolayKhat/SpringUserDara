package com.example.springuserdata.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor
public class SearchUser {

    @NotNull
    @Size(min = 4, max = 4, message = "В серии паспорта должно быть 4 цифры")
    private String passportSeries;

    @NotNull
    @Size(min = 6, max = 6, message = "В номере паспорта должно быть 6 цифры")
    private String passportNumber;
}
