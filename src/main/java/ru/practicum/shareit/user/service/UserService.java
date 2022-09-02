package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto get(Long userId);

    UserDto add(UserDto user);

    UserDto update(Long userId, UserDto user);

    Collection<UserDto> getUsers();

    void remove(Long userId);
}