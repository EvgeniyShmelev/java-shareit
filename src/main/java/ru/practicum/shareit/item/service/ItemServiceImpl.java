package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.comment.CommentRepository;
import ru.practicum.shareit.item.repository.item.ItemRepository;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utill.NumberGenerator;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final NumberGenerator numberGenerator;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    public Item getItemById(long userId, long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("такой вещи нет в списке"));
    }

    public List<ItemDto> getItems(long userId) {
        List<ItemDto> allItems = new ArrayList<>();
        ItemDto itemDto;
        if (userId <= 0) {
            log.info("ID пользователя равен 0");
            throw new ValidationException("ID меньше или равно 0");
        }
        List<Item> items = itemRepository.findAll().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
        for (Item item : items) {
            itemDto = ItemMapper.toItemDto(item);
            allItems.add(itemDto);
        }
        allItems = allItems.stream().sorted(comparing(ItemDto::getId)).collect(Collectors.toList());
        return allItems;
    }

    @Transactional
    public ItemDto add(long userId, ItemDto itemDto) {
        if (userId <= 0 || userService.get(userId) == null) {
            throw new ValidationException("ID меньше или равно 0");
        }
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            log.info("Нет наименования вещи");
            throw new ValidationException("Нет наименования вещи");
        }
        if (itemDto.getDescription() == null) {
            log.info("Нет описания вещи");
            throw new ValidationException("Нет описания вещи");
        }
        if (itemDto.getAvailable() == null) {
            log.info("Нет статуса доступности вещи");
            throw new ValidationException("Отсутствует статус доступности вещи");
        }
        Optional<User> owner = userRepository.findById(userId);
        Item item = ItemMapper.toItem(owner.get(), itemDto, null);
        item.setId(numberGenerator.getId());
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        if (userId <= 0) {
            log.info("ID пользователя равно 0");
            throw new ValidationException("ID пользователя меньше или равно 0");
        }
        if (userService.get(userId) == null) {
            log.info("Пользователь {} не существует", userId);
            throw new ValidationException("Пользователь " + userId + " не существует");
        }
        Optional<User> owner = userRepository.findById(userId);
        Item item = ItemMapper.toItem(owner.get(), itemDto, null);
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    public void delete(long userId, long itemId) {
        if (userId <= 0) {
            log.info("ID пользователя меньше или равно 0");
            throw new ValidationException("ID пользователя меньше или равно 0");
        }
        if (itemId <= 0) {
            log.info("ID вещи меньше или равно 0");
            throw new ValidationException("ID вещи меньше или равно 0");
        }
        itemRepository.deleteById(itemId);
    }

    public List<ItemDto> search(long userId, String search) {
        if (userId <= 0) {
            log.info("ID пользователя меньше 0");
            throw new ValidationException("ID пользователя меньше 0");
        }
        if (search.isEmpty()) {
            log.info("Строка поиска пустая");
            return new ArrayList<>();
        }
        return itemRepository.search(search).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(long itemId, long userId, CommentDto commentDto) {
        if (userId == -1) throw new ValidationException("Не определен заголовок X-Sharer-User-Id");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        List<Booking> booking = bookingRepository.findBookigsToCheckForAddingAComment(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
        if (booking.isEmpty()) throw new ValidationException("Ошибочный запрос");

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        commentDto = CommentMapper.toDto(comment);
        commentDto.setAuthorName(userService.get(userId).getName());
        return commentDto;
    }
}