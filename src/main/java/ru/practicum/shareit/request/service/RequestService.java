package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestListInfoDto;

public interface RequestService {
    ItemRequestListInfoDto addRequest(Long requester, ItemRequestDto itemRequestDto);
}
