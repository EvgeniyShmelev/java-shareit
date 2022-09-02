package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllByBookingId(long userId, Sort start);

    Collection<Booking> findBookingsByStatus(long userId, BookingStatus waiting, Sort start);

    Collection<Booking> findPastBookings(long userId, LocalDateTime now, Sort start);

    Collection<Booking> findFutureBookings(long userId, LocalDateTime now, Sort start);

    Collection<Booking> findCurrentBookings(long userId, LocalDateTime now, Sort start);

    Collection<Booking> findAllByOwnerId(long userId, Sort start);

    Collection<Booking> findBookingsByOwnerIdAndStatus(long userId, BookingStatus waiting, Sort start);

    Collection<Booking> findPastBookingsByOwnerId(long userId, LocalDateTime now, Sort start);

    Collection<Booking> findFutureBookingsByOwnerId(long userId, LocalDateTime now, Sort start);

    Collection<Booking> findCurrentBookingsByOwnerId(long userId, LocalDateTime now, Sort start);
}
