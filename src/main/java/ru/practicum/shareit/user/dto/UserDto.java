package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {

    private long id;        //уникальный идентификатор пользователя

    @NotBlank(groups = UserCreate.class)
    private String name;    //имя или логин пользователя

    @NotBlank(groups = {UserCreate.class})
    @Email(groups = UserCreate.class)
    private String email;   //адрес электронной почты
    //(два пользователя не могут иметь одинаковый адрес электронной почты).

}