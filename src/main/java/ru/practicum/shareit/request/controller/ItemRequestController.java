package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestListInfoDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {

    private final RequestService requestService;

    @PostMapping
    public ItemRequestListInfoDto add(
            @RequestHeader("X-Sharer-User-Id") long requester,
            @Validated(Create.class)
            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST - добавить запрос на вещь");
        return requestService.addRequest(requester, itemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestListInfoDto> findAllByRequester(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET - получить список запросов пользователя с id = `" + userId + "`");
        return requestService.findAllByRequester(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestListInfoDto> findRequestsByParams(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(required = false, defaultValue = "0")
            @PositiveOrZero int from,
            @RequestParam(required = false, defaultValue = "1")
            @Positive int size) {
        log.info("GET - получить список запросов всех пользователей с " +
                "элемента `" + from + "` в количестве `" + size + "`");
        return requestService.findRequestsByParams(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestListInfoDto findByRequestId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long requestId) {
        log.info("GET - получить запрос с id = `" + requestId + "`");
        return requestService.findByRequestId(userId, requestId);
    }
}