package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.sql.Date;

/**
 * Класс представления для бронирования вещи
 */
@Data
public class BookingDto {
    private Long id;        //уникальный идентификатор бронирования;
    private Date start;     //дата начала бронирования;
    private Date end;       //дата конца бронирования;
    private Item item;      //вещь, которую пользователь бронирует;
    private User booker;    //пользователь, который осуществляет бронирование;
    private BookingStatus status;  //статус бронирования.
    //Может принимать одно из следующих значений: WAITING, APPROVED, REJECTED, CANCELED

    public BookingDto() {
    }
}