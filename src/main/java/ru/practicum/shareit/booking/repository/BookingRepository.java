package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findByBooker_Id(Long userId, Sort sort);

    Collection<Booking> findByBooker_IdAndStatus(Long userId, BookingStatus waiting, Sort sort);

    Collection<Booking> findByBooker_IdAndEndIsBefore(Long userId, LocalDateTime end, Sort sort);

    Collection<Booking> findByBooker_IdAndStartIsAfter(Long userId, LocalDateTime start, Sort sort);

    Collection<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime now, LocalDateTime now1, Sort sort);

    Collection<Booking> findByItem_Owner_Id(Long userId, Sort sort);

    Collection<Booking> findByItem_Owner_IdAndStatus(Long userId, BookingStatus waiting, Sort sort);

    Collection<Booking> findByItem_Owner_IdAndEndIsBefore(Long userId, LocalDateTime now, Sort sort);

    Collection<Booking> findByItem_Owner_IdAndStartIsAfter(Long userId, LocalDateTime now, Sort sort);

    Collection<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime now, LocalDateTime now1, Sort sort);

}
