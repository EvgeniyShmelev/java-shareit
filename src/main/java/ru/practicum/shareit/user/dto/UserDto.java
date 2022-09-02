package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {

    private long id;        //уникальный идентификатор пользователя

    @NotBlank(groups = Create.class)
    private String name;    //имя или логин пользователя

    @NotBlank(groups = {Create.class})
    @Email(groups = Create.class)
    private String email;   //адрес электронной почты
    //(два пользователя не могут иметь одинаковый адрес электронной почты).
    public UserDto() {
    }
}