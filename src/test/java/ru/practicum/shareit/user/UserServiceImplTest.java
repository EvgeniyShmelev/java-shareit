package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    public static final long ID = 1L;

    private UserServiceImpl userService;
    private UserRepository userRepository;
    private User user;


    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        user = new User(ID, "Evgeniy", "user1@email.com");
    }

    @Test
    void addUserTest() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto savedDto = UserMapper.toUserDto(user);
        UserDto userDto = userService.add(savedDto);

        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(userDto.getId(), 1);
        Assertions.assertEquals(userDto.getName(), savedDto.getName());
    }

    @Test
    void updateUserTest() {
        user.setName("updated name");
        UserDto savedDto = UserMapper.toUserDto(user);

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user));

        UserDto userDto = userService.update(ID, savedDto);

        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(userDto.getId(), 1);
        Assertions.assertEquals(userDto.getName(), savedDto.getName());
    }

    @Test
    void findUserByIdTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        UserDto userDto = userService.get(ID);

        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(1, userDto.getId());
    }

    @Test
    void findAllUsersTest() {
        when(userRepository.findAll())
                .thenReturn(Collections.singletonList(user));

        Collection<UserDto> listUserDto = userService.getUsers();

        Assertions.assertNotNull(listUserDto);
        Assertions.assertEquals(1, listUserDto.size());
        Assertions.assertEquals(user.getId(), listUserDto.stream().findFirst().get().getId());

    }

    @Test
    void deleteUserByIdTest() {
        userService.remove(ID);
        verify(userRepository, times(1)).deleteById(ID);
    }
}
