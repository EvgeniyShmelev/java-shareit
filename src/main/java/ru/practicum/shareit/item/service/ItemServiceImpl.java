package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentMapper;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemMapper;
import ru.practicum.shareit.item.dto.item.ItemUserDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.comment.CommentRepository;
import ru.practicum.shareit.item.repository.item.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utill.NumberGenerator;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
    private final ModelMapper modelMapper;

    public Item getItemById(long userId, long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("такой вещи нет в списке"));
    }


    public Object getItemDtoById(long userId, long itemId) {
        LocalDateTime dateTime = LocalDateTime.now();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("такой вещи нет в списке"));
        if (itemRepository.findByIdAndOwner_Id(itemId, userId).isPresent()) {
            ItemUserDto toItemDto = ItemMapper.toItemUserDto(item);
            toItemDto.setComments(findAllCommentByItem(item.getId()));
            if (bookingRepository.findLastByItem_IdAndEndIsBefore(itemId, dateTime).isPresent())
                toItemDto.setLastBooking(modelMapper.map(
                        bookingRepository.findLastByItem_IdAndEndIsBefore(itemId, dateTime).get(), BookingDto.class));
            if (bookingRepository.findFirstByItem_IdAndEndIsAfter(itemId, dateTime).isPresent())
                toItemDto.setNextBooking(modelMapper.map(
                        bookingRepository.findFirstByItem_IdAndEndIsAfter(itemId, dateTime).get(), BookingDto.class));
            return toItemDto;
        } else {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            itemDto.setComments(findAllCommentByItem(itemId));
            return itemDto;
        }
    }

    public Object[] getItems(long userId) {
        LocalDateTime dateTime = LocalDateTime.now();
        List<ItemDto> itemList = itemRepository.findAllByOwner_Id(userId)
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        if (itemRepository.findAllByOwner_Id(userId) != null) {
            itemList.forEach(itemOwnerDto -> {
                if (bookingRepository.findLastByItem_IdAndEndIsBefore(itemOwnerDto.getId(), dateTime).isPresent())
                    itemOwnerDto.setLastBooking(modelMapper.map(bookingRepository
                            .findLastByItem_IdAndEndIsBefore(itemOwnerDto.getId(), dateTime).get(), BookingDto.class));
                if (bookingRepository.findFirstByItem_IdAndEndIsAfter(itemOwnerDto.getId(), dateTime).isPresent())
                    itemOwnerDto.setNextBooking(modelMapper.map(bookingRepository
                            .findFirstByItem_IdAndEndIsAfter(itemOwnerDto.getId(), dateTime).get(), BookingDto.class));
            });
            return itemList.toArray();
        } else {
            return itemRepository.findAll()
                    .stream().map(ItemMapper::toItemDto).toArray();
        }
    }

    @Transactional
    public ItemDto add(Long userId, ItemDto itemDto) {
        checkUser(userId);
        User user = UserMapper.toUser(userService.get(userId));
        Item item = ItemMapper.toItem(itemDto);
        item.setId(numberGenerator.getId());
        item.setOwner(user);
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        checkUser(userId);
        Item item = itemRepository.findByIdAndOwner_Id(itemId, userId)
                .orElseThrow(() -> new NotFoundException("неверный идентификатор пользователя"));
        if (itemDto.getName() != null)
            item.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            item.setAvailable(itemDto.getAvailable());
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

    public Collection<ItemDto> search(long userId, String search) {
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
        bookingRepository.findByBooker_IdAndItem_IdAndEndIsBefore(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new ValidationException("Такого пользователя нет"));
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(getItemById(userId, itemId));
        comment.setAuthor(userRepository.findById(userId).get());
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        commentDto = CommentMapper.toDto(comment);
        commentDto.setAuthorName(userService.get(userId).getName());
        return commentDto;
    }

    private Collection<CommentDto> findAllCommentByItem(Long itemId) {
        Collection<Comment> commentList;
        Collection<CommentDto> commentDtoList = new ArrayList<>();
        try {
            commentList = commentRepository.findAllByItem_Id(itemId);
        } catch (NoSuchElementException e) {
            return commentDtoList;
        }
        commentDtoList = commentList.stream().map(CommentMapper::toDto).collect(Collectors.toList());
        commentDtoList.forEach(commentDto ->
                commentDto.setAuthorName(userService.get(commentDto.getAuthorId()).getName()));
        return commentDtoList;
    }

    private void checkUser(Long id) {
        try {
            userService.get(id);
        } catch (NotFoundException e) {
            throw new NotFoundException("такого пользователя нет в списке");
        }
    }
}