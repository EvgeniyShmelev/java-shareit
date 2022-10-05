package ru.practicum.shareit.bookings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.item.ItemMapper;
import ru.practicum.shareit.item.dto.item.ItemUserDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BookingServiceImplTest {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    private Item item;
    private ItemUserDto itemUserDto;
    private User booker;
    private User owner;
    private User thirdUser;
    private Booking booking;
    private BookingDto bookingDto;
    private BookingAddDto bookingAddDto;
    private final LocalDateTime start = LocalDateTime.parse("2022-10-01T01:00");
    private final LocalDateTime end = LocalDateTime.parse("2052-10-01T01:00");

    @BeforeEach
    void beforeEach() {
        owner = new User(1L, "owner", "owner@gmail.com");
        booker = new User(2L, "booker", "booker@gmail.com");
        thirdUser = new User(3L, "thirdUser", "thirdUser@gmail.com");
        owner = UserMapper.toUser(userService.add(UserMapper.toUserDto(owner)));
        booker = UserMapper.toUser(userService.add(UserMapper.toUserDto(booker)));
        thirdUser = UserMapper.toUser(userService.add(UserMapper.toUserDto(thirdUser)));

        item = new Item(1L, "item", "item description", true, owner, null);
        itemUserDto = itemService.add(owner.getId(), ItemMapper.toItemDto(item));

        booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);
        bookingAddDto = modelMapper.map(booking, BookingAddDto.class);
        bookingDto = bookingService.add(booker.getId(), bookingAddDto);
    }

    @Test
    void addBookingTest() {
        assertThat(bookingDto.getId()).isNotZero();
        assertThat(bookingDto.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(bookingDto.getBooker().getName()).isEqualTo(booker.getName());
        assertThat(bookingDto.getStart()).isEqualTo(start);
        assertThat(bookingDto.getEnd()).isEqualTo(end);
        assertThat(bookingDto.getItem().getId()).isEqualTo(item.getId());
        assertThat(bookingDto.getItem().getName()).isEqualTo(item.getName());
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void updateBookingTest() {
        bookingDto.setStatus(BookingStatus.APPROVED);
        assertEquals(bookingDto, bookingService.update(owner.getId(), booking.getId(), "true"));
    }

    @Test
    void getByIdTest() {
        assertEquals(bookingDto, bookingService.getById(owner.getId(), booking.getId()));
    }

    @Test
    void getBookingsByUserAllTest() {
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingsByUser(booker.getId(), BookingState.ALL, 0, 2));
    }

    @Test
    void getBookingsByUserWaitingTest() {
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingsByUser(booker.getId(), BookingState.WAITING, 0, 2));
    }

    @Test
    void getBookingsByUserRejectedTest() {
        bookingDto = bookingService.update(owner.getId(), booking.getId(), "false");
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingsByUser(booker.getId(), BookingState.REJECTED, 0, 2));
    }

    @Test
    void getBookingsByUserPastTest() {
        LocalDateTime start = LocalDateTime.parse("1000-10-05T01:00");
        LocalDateTime end = LocalDateTime.parse("1500-10-05T01:00");
        booking = new Booking(2L, start, end, item, booker, BookingStatus.WAITING);
        bookingAddDto = modelMapper.map(booking, BookingAddDto.class);
        bookingDto = bookingService.add(booker.getId(), bookingAddDto);
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingsByUser(booker.getId(), BookingState.PAST, 0, 2));
    }

    @Test
    void getBookingsByUserFutureTest() {
        LocalDateTime start = LocalDateTime.parse("2023-10-05T01:00");
        LocalDateTime end = LocalDateTime.parse("2024-10-05T01:00");
        booking = new Booking(2L, start, end, item, booker, BookingStatus.WAITING);
        bookingAddDto = modelMapper.map(booking, BookingAddDto.class);
        bookingDto = bookingService.add(booker.getId(), bookingAddDto);
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingsByUser(booker.getId(), BookingState.FUTURE, 0, 2));
    }

    @Test
    void getBookingsByUserIncorrectIdTest() {
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsByUser(-1L, BookingState.ALL, 0, 2));
        assertThat(ex.getMessage()).contains("Записи не найдены");
    }

    @Test
    void getBookingByOwnerAllTest() {
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByOwner(owner.getId(), BookingState.ALL, 0, 2));
    }

    @Test
    void getBookingByOwnerWaitingTest() {
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByOwner(owner.getId(), BookingState.WAITING, 0, 2));
    }

    @Test
    void getBookingByOwnerRejectedTest() {
        bookingDto = bookingService.update(owner.getId(), booking.getId(), "false");
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByOwner(owner.getId(), BookingState.REJECTED, 0, 2));
    }

    @Test
    void getBookingByOwnerPastTest() {
        LocalDateTime start = LocalDateTime.parse("1000-10-05T01:00");
        LocalDateTime end = LocalDateTime.parse("1500-10-05T01:00");
        booking = new Booking(2L, start, end, item, owner, BookingStatus.WAITING);
        bookingAddDto = modelMapper.map(booking, BookingAddDto.class);
        bookingDto = bookingService.add(booker.getId(), bookingAddDto);
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByOwner(owner.getId(), BookingState.PAST, 0, 2));
    }

    @Test
    void getBookingByOwnerFutureTest() {
        LocalDateTime start = LocalDateTime.parse("2023-10-05T01:00");
        LocalDateTime end = LocalDateTime.parse("2024-10-05T01:00");
        booking = new Booking(2L, start, end, item, owner, BookingStatus.WAITING);
        bookingAddDto = modelMapper.map(booking, BookingAddDto.class);
        bookingDto = bookingService.add(booker.getId(), bookingAddDto);
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByOwner(owner.getId(), BookingState.FUTURE, 0, 2));
    }

    @Test
    void getBookingByOwnerIncorrectIdTest() {
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingByOwner(-1L, BookingState.ALL, 0, 2));
        assertThat(ex.getMessage()).contains("Записи не найдены");
    }

    @Test
    void deleteBookingTest() {
        bookingService.deleteBooking(1L, 1L);
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.getById(owner.getId(), booking.getId()));
        assertEquals("такого запроса нет в списке", ex.getMessage());
    }

}
