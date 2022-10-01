package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemMapper;
import ru.practicum.shareit.item.dto.item.ItemUserDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

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
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ModelMapper modelMapper;

    private Item item;
    private ItemUserDto itemUserDto;
    private ItemDto itemDto;
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

        assertEquals(itemUserDto, itemService.update(user.getId(), itemUserDto.getId(), updatedItem));
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

    @Test
    void deleteItem() {
        itemService.delete(user.getId(), item.getId());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemService.getItemById(user.getId(), item.getId()));
        assertEquals("такой вещи нет в списке", ex.getMessage());
    }

    @Test
    void deleteItemIncorrectUserId() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> itemService.delete(-1, item.getId()));
        assertEquals("ID пользователя меньше или равно 0", ex.getMessage());
    }

    @Test
    void deleteItemIncorrectItemId() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> itemService.delete(user.getId(), 0));
        assertEquals("ID вещи меньше или равно 0", ex.getMessage());
    }

    @Test
    void getItemEachUserById_Owner_WithNextBooking() {
        User booker = new User(3L, "booker", "user3@email.com");
        UserDto bookerDto = userService.add(UserMapper.toUserDto(booker));

        LocalDateTime start = LocalDateTime.parse("2100-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("2200-09-01T01:00");

        Booking booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);

        BookingAddDto bookItemRequestDto = modelMapper.map(booking, BookingAddDto.class);
        BookingDto bookingDto = bookingService.add(bookerDto.getId(), bookItemRequestDto);

        User owner = UserMapper.toUser(userDto);
        item.setOwner(owner);

        ItemUserDto itemUserDto = ItemMapper.toItemUserDto(item);
        itemUserDto.setComments(new ArrayList<>());
        itemUserDto.setNextBooking(bookingDto);

        assertEquals(itemUserDto, itemService.getItemDtoById(userDto.getId(), item.getId()));
    }

    @Test
    void getItemEachUserById_Owner_WithLastBooking() {
        User booker = new User(4L, "booker", "user4@email.com");
        UserDto bookerDto = userService.add(UserMapper.toUserDto(booker));

        LocalDateTime start = LocalDateTime.parse("1100-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("1200-09-01T01:00");

        Booking booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);

        BookingAddDto bookItemRequestDto = modelMapper.map(booking, BookingAddDto.class);
        BookingDto bookingDto = bookingService.add(bookerDto.getId(), bookItemRequestDto);

        User owner = UserMapper.toUser(userDto);
        item.setOwner(owner);

        ItemUserDto itemUserDto = ItemMapper.toItemUserDto(item);
        itemUserDto.setComments(new ArrayList<>());
        itemUserDto.setLastBooking(bookingDto);

        assertEquals(itemUserDto, itemService.getItemDtoById(userDto.getId(), item.getId()));
    }

    @Test
    void getAllItemsWithNextBooking() {
        User booker = new User(5L, "booker", "user5@email.com");
        UserDto bookerDto = userService.add(UserMapper.toUserDto(booker));

        LocalDateTime start = LocalDateTime.parse("2100-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("2200-09-01T01:00");

        Booking booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);
        BookingAddDto bookItemRequestDto = modelMapper.map(booking, BookingAddDto.class);
        BookingDto bookingDto = bookingService.add(bookerDto.getId(), bookItemRequestDto);

        ItemUserDto itemUserDto = ItemMapper.toItemUserDto(item);

        itemUserDto.setNextBooking(bookingDto);

        Object[] itemsList = itemService.getItems(1L);

        for (Object o : itemsList) {
            itemDto = (ItemDto) o;
            itemUserDto = modelMapper.map(itemDto, ItemUserDto.class);

            assertEquals(1, itemsList.length);
            assertEquals("item", itemDto.getName());
            assertEquals("description", itemDto.getDescription());
            assertEquals(1, itemUserDto.getNextBooking().getId());
        }
    }

    @Test
    void getAllItemsWithLastBooking() {
        User booker = new User(6L, "booker", "user6@email.com");
        UserDto bookerDto = userService.add(UserMapper.toUserDto(booker));

        LocalDateTime start = LocalDateTime.parse("1100-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("1200-09-01T01:00");

        Booking booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);
        BookingAddDto bookItemRequestDto = modelMapper.map(booking, BookingAddDto.class);
        BookingDto bookingDto = bookingService.add(bookerDto.getId(), bookItemRequestDto);

        ItemUserDto itemUserDto = ItemMapper.toItemUserDto(item);
        itemUserDto.setLastBooking(bookingDto);

        Object[] itemsList = itemService.getItems(1L);

        for (Object o : itemsList) {
            itemDto = (ItemDto) o;
            itemUserDto = modelMapper.map(itemDto, ItemUserDto.class);

            assertEquals(1, itemsList.length);
            assertEquals("item", itemDto.getName());
            assertEquals("description", itemDto.getDescription());
            assertEquals(1, itemUserDto.getLastBooking().getId());
        }
    }

    @Test
    void search() {
        String text = "ПКРЦМ";
        Item item = new Item(2L, "ПКРЦМ", "КОМАГ",
                true, user, null);
        Item item1 = new Item(3L, "APPLE", "IPHONE",
                true, user, null);
        itemUserDto = itemService.add(userDto.getId(), ItemMapper.toItemDto(item));
        Collection<ItemDto> itemsList = itemService.search(1L, text);

        assertEquals(1, itemsList.size());
        assertEquals("КОМАГ", new ArrayList<>(itemsList).get(0).getDescription());
    }

}
