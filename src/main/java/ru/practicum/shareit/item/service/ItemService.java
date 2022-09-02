package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public interface ItemService {

    Item getItemById(long userId, long itemId);

    List<ItemDto> getItems(long userId);

    ItemDto add(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    List<ItemDto> search(long userId, String searchItem);

    void delete(long userId, long itemId);

    CommentDto addComment(long itemId, long userId, CommentDto comment);
}