package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemMapper;
import ru.practicum.shareit.item.dto.item.ItemUserDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
//Нужен интеграционный тест, тк я не могу связать вещь с пользователем
public class ItemServiceImplTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    private Item item;
    private ItemDto itemDto;
    private ItemUserDto itemUserDto;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "Evgeniy", "user1@email.com");
        userDto = userService.add(UserMapper.toUserDto(user));
        item = new Item(1L, "item", "description", true, null, null);
        itemUserDto = itemService.add(userDto.getId(), ItemMapper.toItemDto(item));
    }

    @Test
    void addItem() {
        assertThat(itemUserDto.getName()).isEqualTo("item");
        assertThat(itemUserDto.getDescription()).isEqualTo("description");
        assertThat(itemUserDto.getAvailable()).isTrue();
        assertThat(itemUserDto.getRequestId()).isNull();
    }

    @Test
    void updateItemWithNameAndDescriptionAndAvalibale() {
        ItemDto updatedItem = new ItemDto();
        updatedItem.setName("itemNew");
        updatedItem.setDescription("descriptionNew");
        updatedItem.setAvailable(false);
        itemUserDto.setName("itemNew");
        itemUserDto.setDescription("descriptionNew");
        itemUserDto.setAvailable(false);

        assertEquals(itemUserDto, itemService.update(user.getId(), item.getId(), updatedItem));
    }

    @Test
    void updateItemWithNotOwner() {
        ItemDto updatedItem = new ItemDto();
        updatedItem.setName("updated name");
        itemUserDto.setName("updated name");
        User userNotOwner = new User(2L, "EvgeniyNotOwner", "user2@email.com");
        UserDto userDtoNotOwner = userService.add(UserMapper.toUserDto(userNotOwner));
        long idUserNotOwner = userDtoNotOwner.getId();

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemService.update(idUserNotOwner, item.getId(), updatedItem));
        assertEquals("неверный идентификатор пользователя", ex.getMessage());
    }

}
