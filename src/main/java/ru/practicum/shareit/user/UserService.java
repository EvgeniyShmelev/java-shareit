package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto get(long userId);

    UserDto add(UserDto user);

    UserDto update(long userId, UserDto user);

    Collection<UserDto> getUsers();

    void remove(long userId);
}