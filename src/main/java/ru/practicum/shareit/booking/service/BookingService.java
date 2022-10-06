package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;
import java.util.List;

public interface BookingService {

    BookingDto add(Long  userId, BookingAddDto bookingAddDto);

    BookingDto update(Long  userId, Long  bookingId, String approved);

    //Получение данных о конкретном бронировании
    BookingDto getById(Long  userId, Long  bookingId);

    //Получение списка всех бронирований текущего пользователя.
    List<BookingDto> getBookingsByUser(Long  userId, BookingState state, int from, int size);

    //Получение списка бронирований для всех вещей текущего пользователя
    List<BookingDto> getBookingByOwner(Long  userId, BookingState state, int from, int size);

    void deleteBooking(Long  userId, Long  bookingId);

}
