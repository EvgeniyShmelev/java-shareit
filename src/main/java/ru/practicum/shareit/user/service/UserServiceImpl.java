package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utill.NumberGenerator;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final NumberGenerator numberGenerator;

    @Override
    public UserDto get(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("такого пользователя нет в списке"));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto add(UserDto userDto) {
        checkUser(userDto);
        User user = UserMapper.toUser(userDto);
        user.setId(numberGenerator.getId());
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        checkUser(userDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("такого пользователя нет в списке"));
        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());
        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public Collection<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void remove(Long userId) {
        if (userId <= 0) {
            log.info("ID пользователя равен 0");
            throw new ValidationException("ID пользователя меньше или равно 0");
        }
        userRepository.deleteById(userId);
    }

    private void checkUser(UserDto userDto) {
        userRepository.findByEmail(userDto.getEmail()).ifPresent((u) -> {
                    try {
                        throw new SQLException("пользователь с таким ящиком уже существует " + u);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
        );
    }
}