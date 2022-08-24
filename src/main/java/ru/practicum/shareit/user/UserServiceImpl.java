package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utill.NumberGenerator;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final NumberGenerator numberGenerator;

    public UserDto get(long userId) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("такого пользователя нет в списке"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto add(UserDto userDto) {
        if (checkEmail(userDto)){
            User user = UserMapper.toUser(userDto);
            user.setId(numberGenerator.getUserId());
            return UserMapper.toUserDto(userRepository.createUser(user));
        }
        log.info("Класс сервис");
        throw new AlreadyExistException("Такой пользователь уже существует");
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        if (userId <= 0) {
            log.info("ID пользователя равен 0");
            throw new ValidationException("ID пользователя равен 0");
        }
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.updateUser(userId, user));
    }

    public Collection<UserDto> getUsers() {
        return userRepository.getUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public void remove(long userId) {
        if (userId <= 0) {
            log.info("ID пользователя равен 0");
            throw new NullPointerException("ID пользователя меньше или равно 0");
        }
        userRepository.removeUser(userId);
    }

    private boolean checkEmail(UserDto user) {
        return userRepository.checkEmail(user.getEmail());
    }
}