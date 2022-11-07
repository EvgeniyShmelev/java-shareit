package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        log.info("Список всех пользователей");
        return userClient.findAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Создан пользователь {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping(value = "/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                             @Validated({Update.class})
                                             @RequestBody UserDto userDto) {
        log.info("Создан пользователь {}", userDto);
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<Object> findUserById(@PathVariable Long userId) {
        log.info("Получен пользователь пользователь={}", userId);
        return userClient.findUserById(userId);
    }

    @DeleteMapping(value = "/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Удален пользователь={}", userId);
        userClient.deleteUserById(userId);
    }
}
