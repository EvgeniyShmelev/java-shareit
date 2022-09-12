package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestListInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final ModelMapper modelMapper;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    public ItemRequestListInfoDto addRequest(Long requester, ItemRequestDto itemRequestDto) {
        User user = validateUser(requester);
        ItemRequest request = modelMapper.map(itemRequestDto, ItemRequest.class);
        request.setRequester(user);
        requestRepository.save(request);
        return modelMapper.map(request, ItemRequestListInfoDto.class);
    }

    private User validateUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("такого пользователя нет в списке"));
    }
}
