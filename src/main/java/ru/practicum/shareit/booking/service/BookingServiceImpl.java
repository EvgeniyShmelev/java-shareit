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
    public BookingDto add(Long userId, BookingDto bookingDto) {
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
    public BookingDto update(Long userId, Long bookingId, String approved) {
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
    public BookingDto getById(Long userId, Long bookingId) {
        userRepository.findById(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("такого запроса нет в списке"));
        if (booking.getBooker().getId().equals(bookingId) || booking.getItem().getOwner().getId().equals(userId))
            return BookingMapper.toBookingDto(booking);
        else
            throw new NotFoundException("неверный идентификатор пользователя");
    }

    @Override
    public Collection<BookingDto> getBookingsByUser(Long userId, BookingState state) {
        Collection<Booking> bookings;
        bookings = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime dateTime = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBooker_Id(userId, sort);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.WAITING, sort);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.REJECTED, sort);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, dateTime, sort);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfter(userId, dateTime, sort);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, dateTime, dateTime, sort);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getBookingByOwner(Long userId, BookingState state) {
        Collection<Booking> bookings;
        bookings = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime dateTime = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItem_Owner_Id(userId, sort);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case WAITING:
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(userId, BookingStatus.WAITING, sort);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case REJECTED:
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(userId, BookingStatus.REJECTED, sort);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case PAST:
                bookings = bookingRepository.findByItem_Owner_IdAndEndIsBefore(userId, dateTime, sort);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case FUTURE:
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsAfter(userId, dateTime, sort);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
            case CURRENT:
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, dateTime, dateTime, sort);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBooking(Long userId, Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }
}

