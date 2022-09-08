package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setBookerId(booking.getBooker().getId());
        bookingDto.setItem(
                new BookingDto.BookingItem(booking.getItem().getId(), booking.getItem().getName()));
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBooker(
                new BookingDto.BookingUser(booking.getBooker().getId()));
        return bookingDto;
    }

    public static Booking toBooking(BookingAddDto bookingAddDto) {

        Booking booking = new Booking();
        booking.setStart(bookingAddDto.getStart());
        booking.setEnd(bookingAddDto.getEnd());
        return booking;
    }
}