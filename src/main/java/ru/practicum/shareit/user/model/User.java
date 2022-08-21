package ru.practicum.shareit.user.model;

import lombok.Data;
import ru.practicum.shareit.user.dto.UserCreate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class User {

    private long id;        //уникальный идентификатор пользователя

    private String name;    //имя или логин пользователя

    @Email(groups = UserCreate.class)
    @NotBlank(groups = UserCreate.class)
    private String email;   //адрес электронной почты
    //(два пользователя не могут иметь одинаковый адрес электронной почты).
}