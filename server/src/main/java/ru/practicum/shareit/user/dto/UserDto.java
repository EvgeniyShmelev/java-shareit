package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserDto {

    private Long id;        //уникальный идентификатор пользователя
    private String name;    //имя или логин пользователя
    private String email;   //адрес электронной почты

    public UserDto() {
    }
}