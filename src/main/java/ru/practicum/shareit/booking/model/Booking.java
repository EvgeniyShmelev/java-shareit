package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.sql.Date;

/**
 * Класс бронирования
 */
@Data
@Entity
@Table(name = "bookings", schema = "public")
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;                //уникальный идентификатор бронирования;

    @Column(name = "start_date")
    private Date start;             //дата начала бронирования;

    @Column(name = "end_date")
    private Date end;               //дата конца бронирования;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private Item item;              //вещь, которую пользователь бронирует;

    @ManyToOne(optional = false)
    @JoinColumn(name = "booker_id")
    private User booker;            //пользователь, который осуществляет бронирование;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;   //статус бронирования.
    //Может принимать одно из следующих значений: WAITING, APPROVED, REJECTED, CANCELED
}