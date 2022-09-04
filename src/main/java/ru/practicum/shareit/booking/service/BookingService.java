package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {

    BookingDto add(Long  userId, BookingDto bookingDto);

    BookingDto update(Long  userId, Long  bookingId, String approved);

    //Получение данных о конкретном бронировании
    BookingDto getById(Long  userId, Long  bookingId);

    //Получение списка всех бронирований текущего пользователя.
    Collection<BookingDto> getBookingsByUser(Long  userId, BookingState state);

    //Получение списка бронирований для всех вещей текущего пользователя
    Collection<BookingDto> getBookingByOwner(Long  userId, BookingState state);

    void deleteBooking(Long  userId, Long  bookingId);

}
