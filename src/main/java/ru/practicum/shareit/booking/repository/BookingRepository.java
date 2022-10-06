package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long userId, Pageable pageable);

    List<Booking> findByBooker_IdAndStatus(Long userId, BookingStatus waiting, Pageable pageable);

    List<Booking> findByBooker_IdAndEndIsBefore(Long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findByBooker_IdAndStartIsAfter(Long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime now, LocalDateTime now1, Pageable pageable);

    List<Booking> findByItem_Owner_Id(Long userId, Pageable sort);

    List<Booking> findByItem_Owner_IdAndStatus(Long userId, BookingStatus waiting, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndEndIsBefore(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartIsAfter(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime now, LocalDateTime now1, Pageable pageable);

    Optional<Booking> findByBooker_IdAndItem_IdAndEndIsBefore(Long booker, Long item, LocalDateTime end);

    Optional<Booking> findLastByItem_IdAndEndIsBefore(Long item, LocalDateTime end);

    Optional<Booking> findFirstByItem_IdAndEndIsAfter(Long item, LocalDateTime start);

}
