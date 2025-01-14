package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public BookingDto add(Long userId, BookingAddDto bookingAddDto) {
        User user = UserMapper.toUser(userService.get(userId));
        Item item = itemService.getItemById(userId, bookingAddDto.getItemId());
        if (!item.getAvailable())
            throw new ValidationException("вещь недоступна");
        if (bookingAddDto.getStart().isAfter(bookingAddDto.getEnd()))
            throw new ValidationException("неверно указано время");
        if (item.getOwner().getId().equals(userId))
            throw new NotFoundException("неверный идентификатор пользователя");
        Booking booking = modelMapper.map(bookingAddDto, Booking.class);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Transactional
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
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        userService.get(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("такого запроса нет в списке"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))
            return modelMapper.map(booking, BookingDto.class);
        else
            throw new NotFoundException("неверный идентификатор пользователя");
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId, BookingState state, int from, int size) {
        List<Booking> bookings;
        bookings = new ArrayList<>();
        LocalDateTime dateTime = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of(from / size, size, sort);
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBooker_Id(userId, pageable);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.WAITING, pageable);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.REJECTED, pageable);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, dateTime, pageable);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfter(userId, dateTime, pageable);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
                break;
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, dateTime, dateTime, pageable);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
                break;
        }

        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingByOwner(Long userId, BookingState state, int from, int size) {
        Collection<Booking> bookings;
        bookings = new ArrayList<>();
        LocalDateTime dateTime = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of(from, size, sort);
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItem_Owner_Id(userId, pageable);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
                break;
            case WAITING:
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(userId, BookingStatus.WAITING, pageable);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(userId, BookingStatus.REJECTED, pageable);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
                break;
            case PAST:
                bookings = bookingRepository.findByItem_Owner_IdAndEndIsBefore(userId, dateTime, pageable);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsAfter(userId, dateTime, pageable);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
                break;
            case CURRENT:
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, dateTime, dateTime, pageable);
                if (bookings.isEmpty()) throw new NotFoundException("Записи не найдены");
                break;
        }

        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteBooking(Long userId, Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }
}

