package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.repository.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestListInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final ModelMapper modelMapper;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestListInfoDto addRequest(Long requester, ItemRequestDto itemRequestDto) {
        ItemRequest request = modelMapper.map(itemRequestDto, ItemRequest.class);
        request.setRequester(validateUser(requester));
        request.setCreated(LocalDateTime.now());
        ItemRequestListInfoDto itemRequestListInfoDto = modelMapper
                .map(requestRepository.save(request), ItemRequestListInfoDto.class);

        Collection<ItemDto> itemList = itemRepository.findByRequest_Id(requester)
                .stream()
                .map(itemRequest -> modelMapper.map(itemRequest, ItemDto.class))
                .collect(Collectors.toList());

        itemRequestListInfoDto.setItems(itemList);

        return itemRequestListInfoDto;
    }

    public Collection<ItemRequestListInfoDto> findAllByRequester(Long userId) {
        validateUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return requestRepository.findByRequester_Id(userId, sort)
                .stream()
                .map(itemRequest -> modelMapper.map(itemRequest, ItemRequestListInfoDto.class))
                .collect(Collectors.toList());
    }

    public Collection<ItemRequestListInfoDto> findRequestsByParams(Long userId, int from, int size) {
        if (size == 0) throw new ValidationException("неверно указан размер списка");
        validateUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");

        return requestRepository
                .findByRequester_IdIsNot(userId, PageRequest.of(from, size, sort))
                .stream()
                .map(itemRequest -> modelMapper.map(itemRequest, ItemRequestListInfoDto.class))
                .collect(Collectors.toList());
    }

    public ItemRequestListInfoDto findByRequestId(Long userId, Long requestId) {
        validateUser(userId);
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        return modelMapper.map(itemRequest, ItemRequestListInfoDto.class);
    }

    private User validateUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("такого пользователя нет в списке"));
    }
}
