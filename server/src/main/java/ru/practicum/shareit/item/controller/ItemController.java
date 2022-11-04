package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemUserDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemUserDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                           @RequestBody ItemDto itemDto) {
        log.info("Добавлена вещь: {}", itemDto);
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemUserDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Обновлена вещь: {}", itemDto);
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public Object getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId) {
        log.info("Получен запрос вещи по ID");
        return itemService.getItemDtoById(userId, itemId);
    }

    @GetMapping
    public Object[] get(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос cписка вещей");
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam String text) {
        log.info("Получен запрос вещей по наименованию");
        return itemService.search(userId, text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        log.info("Получен запрос на удаление вещи");
        itemService.delete(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentDto commentDto,
                                 @PathVariable long itemId,
                                 @RequestHeader(value = "X-Sharer-User-Id",
                                         defaultValue = "-1") long userId) {
        log.info("Добавление комментария к вещи с id = `" + itemId + "`");
        return itemService.addComment(itemId, userId, commentDto);
    }
}