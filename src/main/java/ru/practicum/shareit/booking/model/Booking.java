package ru.practicum.shareit.booking.model;

import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

/**
 * Класс бронирования
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                //уникальный идентификатор бронирования;

    @Column(name = "start_date")
    private Date start;             //дата начала бронирования;

    @Column(name = "end_date")
    private Date end;               //дата конца бронирования;

    @OneToOne (fetch = FetchType.EAGER)
    @JoinColumn (name = "item_id")
    private Item item;              //вещь, которую пользователь бронирует;

    @OneToOne (fetch = FetchType.EAGER)
    @JoinColumn (name = "booker_id")
    private User booker;            //пользователь, который осуществляет бронирование;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;   //статус бронирования.
    //Может принимать одно из следующих значений: WAITING, APPROVED, REJECTED, CANCELED

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Booking booking = (Booking) o;
        return id != null && Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}