package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {

    BookingDto add(long userId, BookingDto bookingDto);

    BookingDto update(long userId, long bookingId, String approved);

    //Получение данных о конкретном бронировании
    BookingDto getById(long userId, long bookingId);

    //Получение списка всех бронирований текущего пользователя.
    Collection<BookingDto> getBookingsByUser(long userId, BookingState state);

    //Получение списка бронирований для всех вещей текущего пользователя
    Collection<BookingDto> getBookingByOwner(long userId, BookingState state);

    void deleteBooking(long userId, long bookingId);

}
