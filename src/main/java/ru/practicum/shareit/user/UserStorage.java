package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Component
@Repository
public class UserStorage implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emailUniqSet = new HashSet<>();

    public User createUser(User user) {
        users.put(user.getId(), user);
        emailUniqSet.add(user.getEmail());
        return user;
    }

    @Override
    public Optional<User> findUserById(long userId) {
        try {
            return Optional.ofNullable(users.get(userId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public User updateUser(long userId, User user) {
        emailUniqSet.remove(getUser(userId).getEmail());
        User newUser = users.get(userId);
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        if (!checkEmail(user.getEmail())) {
            log.info("Email уже существует");
            throw new AlreadyExistException("Email уже существует");
        }
        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        users.put(userId, newUser);
        log.info("Обновлены данные пользователя ID# " + userId);
        return users.get(userId);
    }

    @Override
    public void removeUser(long userId) {
        emailUniqSet.remove(getUser(userId).getEmail());
        users.remove(userId);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public UserDto getUser(long userId) {
        return UserMapper.toUserDto(users.get(userId));
    }

    @Override
    public boolean checkEmail(String userEmail) {
        return !emailUniqSet.contains(userEmail);
    }
}