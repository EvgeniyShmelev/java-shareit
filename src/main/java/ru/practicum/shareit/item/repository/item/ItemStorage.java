package ru.practicum.shareit.item.repository.item;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

//@Component
@Slf4j
public class ItemStorage /*implements ItemRepository*/ {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> userItemIndex = new LinkedHashMap<>();
    private long id;

    //@Override
    public List<ItemDto> findByUserId(long userId) {
        return userItemIndex.get(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    //@Override
    public List<Item> getListItems() {
        return userItemIndex.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    //@Override
    public ItemDto getItemById(long itemId) {
        return userItemIndex.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .stream()
                .map(ItemMapper::toItemDto)
                .findAny().orElse(null);
    }

    //@Override
    public Item save(Item item) {
        userItemIndex.computeIfAbsent(item.getOwner().getId(), k -> new ArrayList<>());
        userItemIndex.get(item.getOwner().getId()).add(item);
        items.put(item.getId(), item);
        return item;
    }

    //@Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        for (Item item : userItemIndex.get(userId)) {
            if (item.getId() == itemId) {
                items.remove(item);
                log.info("Вещь ID# {} удалена", itemId);
            }
        }
    }

    //@Override
    public ItemDto updateItem(User user, long itemId, ItemDto itemDto) {
        if (!userItemIndex.containsKey(user.getId())) {
            log.info("У пользователя нет вещей");
            throw new NotFoundException("У пользователя нет вещей");
        }
        Item item = ItemMapper.toItem(user, itemDto, null);
        Item newItem = null;
        for (Item i : userItemIndex.get(user.getId())) {
            if (i.getId() == itemId) {
                if (item.getName() != null) {
                    i.setName(item.getName());
                }
                if (item.getDescription() != null) {
                    i.setDescription(item.getDescription());
                }
                if (item.getAvailable() != null) {
                    i.setAvailable(item.getAvailable());
                }
                newItem = i;
                break;
            }
        }
        if(newItem == null){
            log.info("Вещь не найдена");
            throw new NotFoundException("Вещь не найдена");
        }
        log.info("Вещь ID# {} обновлена", itemId);
        return ItemMapper.toItemDto(newItem);
    }

    public List<ItemDto> searchItem(String search) {
        List<ItemDto> resultSearchItem = new ArrayList<>();
        for (Item item : getListItems()) {
            if (item.getName().toUpperCase().contains(search.toUpperCase()) ||
                    item.getDescription().toUpperCase().contains(search.toUpperCase())) {
                if (item.getAvailable()) {
                    resultSearchItem.add(ItemMapper.toItemDto(item));
                }
            }
        }
        return resultSearchItem;
    }
}