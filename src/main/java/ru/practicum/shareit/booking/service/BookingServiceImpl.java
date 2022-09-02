package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.item.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utill.NumberGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final NumberGenerator numberGenerator;

    @Override
    public BookingDto add(long userId, BookingDto bookingDto) {
        User user = userRepository.getReferenceById(userId);
        Item item = itemRepository.getReferenceById(bookingDto.getItem().getId());
        if (!item.getAvailable())
            throw new ValidationException("вещь недоступна");
        if (bookingDto.getStart().after(bookingDto.getEnd()))
            throw new ValidationException("неверно указано время");
        if (item.getOwner().getId().equals(userId))
            throw new NotFoundException("неверный идентификатор пользователя");
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setId(numberGenerator.getId());
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto update(long userId, long bookingId, String approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("такого запроса нет в списке"));
        if (!booking.getItem().getOwner().getId().equals(userId))
            throw new NotFoundException("неверный идентификатор пользователя");
        if (booking.getStatus().equals(BookingStatus.APPROVED))
            throw new ValidationException("невозможно изменить статус");
        if (approved.equals("true")) {
            booking.setStatus(BookingStatus.APPROVED);
        } else booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getById(long userId, long bookingId) {
        userRepository.findById(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("такого запроса нет в списке"));
        if (booking.getBooker().getId().equals(bookingId) || booking.getItem().getOwner().getId().equals(userId))
            return BookingMapper.toBookingDto(booking);
        else
            throw new NotFoundException("неверный идентификатор пользователя");
    }

    @Override
    public Collection<BookingDto> getBookingsByUser(long userId, BookingState state) {
        Collection<Booking> bookings;
        bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookingId(
                        userId, Sort.by(Sort.Direction.DESC, "start"));
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case WAITING:
                bookings = bookingRepository.findBookingsByStatus(
                        userId, BookingStatus.WAITING, Sort.by(Sort.Direction.DESC, "start"));
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case REJECTED:
                bookings = bookingRepository.findBookingsByStatus(
                        userId, BookingStatus.REJECTED, Sort.by(Sort.Direction.DESC, "start"));
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case PAST:
                bookings = bookingRepository.findPastBookings(
                        userId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case FUTURE:
                bookings = bookingRepository.findFutureBookings(
                        userId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case CURRENT:
                bookings = bookingRepository.findCurrentBookings(
                        userId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getBookingByOwner(long userId, BookingState state) {
        Collection<Booking> bookings;
        bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByOwnerId(
                        userId, Sort.by(Sort.Direction.DESC, "start"));
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case WAITING:
                bookings = bookingRepository.findBookingsByOwnerIdAndStatus(
                        userId, BookingStatus.WAITING, Sort.by(Sort.Direction.DESC, "start"));
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case REJECTED:
                bookings = bookingRepository.findBookingsByOwnerIdAndStatus(
                        userId, BookingStatus.REJECTED, Sort.by(Sort.Direction.DESC, "start"));
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case PAST:
                bookings = bookingRepository.findPastBookingsByOwnerId(
                        userId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByOwnerId(
                        userId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByOwnerId(
                        userId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBooking(long userId, long bookingId) {
        bookingRepository.deleteById(bookingId);
    }
}

