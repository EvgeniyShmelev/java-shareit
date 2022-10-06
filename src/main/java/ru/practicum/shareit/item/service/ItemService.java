package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemUserDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Service
public interface ItemService {

    Item getItemById(long userId, long itemId);

    Object getItemDtoById(long userId, long itemId);

    Object[] getItems(long userId);

    ItemUserDto add(Long userId, ItemDto itemDto);

    ItemUserDto update(long userId, long itemId, ItemDto itemDto);

    Collection<ItemDto> search(long userId, String searchItem);

    void delete(long userId, long itemId);

    CommentDto addComment(long itemId, long userId, CommentDto comment);
}