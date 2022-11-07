package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    public static final String DEFAULT_FROM_VALUE = "0";
    public static final String DEFAULT_SIZE_VALUE = "20";
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Validated({Create.class})
                                             @RequestBody ItemDto itemDto,
                                             @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.createItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Validated({Create.class})
                                                @RequestBody CommentDto commentDto,
                                                @PathVariable Long itemId,
                                                @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.createComment(commentDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Validated({Update.class})
                                             @RequestBody ItemDto itemDto,
                                             @PathVariable Long itemId,
                                             @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@PathVariable Long itemId,
                                               @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.findItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItems(@RequestHeader(USER_ID_HEADER) Long userId,
                                               @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                               @PositiveOrZero int from,
                                               @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                               @Positive int size) {
        return itemClient.findAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemsByRequest(@RequestParam String text,
                                                     @RequestHeader(USER_ID_HEADER) Long userId,
                                                     @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                     @PositiveOrZero int from,
                                                     @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                     @Positive int size) {
        return itemClient.findItemsByRequest(text, userId, from, size);
    }
}