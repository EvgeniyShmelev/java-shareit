package ru.practicum.shareit.item.dto.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static ItemUserDto toItemUserDto(Item item) {
        ItemUserDto toItemUserDto = new ItemUserDto();
        toItemUserDto.setId(item.getId());
        toItemUserDto.setName(item.getName());
        toItemUserDto.setDescription(item.getDescription());
        toItemUserDto.setAvailable(item.getAvailable());
        if (item.getRequest() != null)
            toItemUserDto.setRequestId(item.getRequest().getId());
        return toItemUserDto;
    }
}