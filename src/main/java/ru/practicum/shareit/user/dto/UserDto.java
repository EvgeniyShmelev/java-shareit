package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {

    private Long id;        //уникальный идентификатор пользователя

    @NotBlank(groups = {Create.class})
    private String name;    //имя или логин пользователя

    @NotBlank(groups = {Create.class})
    @Email(groups = {Update.class, Create.class})
    private String email;   //адрес электронной почты
    //(два пользователя не могут иметь одинаковый адрес электронной почты).
    public UserDto() {
    }
}