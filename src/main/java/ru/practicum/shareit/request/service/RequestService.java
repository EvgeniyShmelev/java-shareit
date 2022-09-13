package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestListInfoDto;

import java.util.Collection;

public interface RequestService {

    ItemRequestListInfoDto addRequest(Long requester, ItemRequestDto itemRequestDto);

    Collection<ItemRequestListInfoDto> findAllByRequester(Long userId);

    Collection<ItemRequestListInfoDto> findRequestsByParams(Long userId, int from, int size);

    ItemRequestListInfoDto findByRequestId(Long userId, Long requestId);
}
