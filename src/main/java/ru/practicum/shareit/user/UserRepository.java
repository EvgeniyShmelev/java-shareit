package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findUserById(long userId);

    User createUser(User user);

    User updateUser(long userId, User user);

    void removeUser(long userId);

    List<User> getUsers();

    boolean checkEmail(String userEmail);

}