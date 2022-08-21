package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    User get(long userId);

    User add(User user);

    User update(long userId, User user);

    Collection<User> getUsers();

    void remove(long userId);
}