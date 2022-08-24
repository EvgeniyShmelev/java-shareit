package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository {
    List<ItemDto> findByUserId(long userId);

    Item save(Item Item);

    List<Item> getListItems();

    ItemDto getItemById(long itemId);

    void deleteByUserIdAndItemId(long userId, long itemId);

    ItemDto updateItem(User user, long itemId, ItemDto itemDto);

    List<ItemDto> searchItem(String search);
}