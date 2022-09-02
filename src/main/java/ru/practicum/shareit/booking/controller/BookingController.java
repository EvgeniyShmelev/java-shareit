package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Validated(Create.class) @RequestBody BookingDto bookingDto) {
        log.debug("Добавлен запрос на бронирование: {}", bookingDto);
        return bookingService.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId,
                                   @RequestParam String approved) {
        log.debug("Обновлено бронирование: {}", bookingId);
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        log.debug("Получен запрос бронирования вещи ID {}", bookingId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping()
    public Collection<BookingDto> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestParam(value = "state", required = false) BookingState state) {
        log.debug("Получен запрос бронирований пользователя {}", userId);
        return bookingService.getBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getBookingByIdByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(value = "state", required = false) BookingState state) {
        log.debug("Получен запрос бронирования вещи пользователя {}", userId);
        return bookingService.getBookingByOwner(userId, state);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long bookingId) {
        log.debug("Получен запрос на удаление бронирования ID {}", bookingId);
        bookingService.deleteBooking(userId, bookingId);
    }
}