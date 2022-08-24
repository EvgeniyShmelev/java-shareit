package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public UserDto create(@Validated(UserCreate.class) @RequestBody UserDto user) {
        return userService.add(user);
    }

    @PatchMapping(value = "/{id}")
    public UserDto update(@PathVariable long id, @RequestBody UserDto  user) {
        return userService.update(id, user);
    }

    @GetMapping(value = "/{id}")
    public UserDto getUser(@PathVariable long id) throws NotFoundException {
        return userService.get(id);
    }

    @DeleteMapping(value = "/{id}")
    public void removeUser(@PathVariable long id) {
        userService.remove(id);
    }
}