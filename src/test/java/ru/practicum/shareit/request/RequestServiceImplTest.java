package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestListInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RequestServiceImplTest {
    @Autowired
    private RequestService requestService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;
    private UserDto requesterDto;
    private ItemRequestDto itemRequestDto;
    private ItemRequestListInfoDto itemRequestListInfoDto;

    @BeforeEach
    void beforeEach() {
        User requester = new User(1L, "Evgeniy", "Evgeniy@mail.ru");
        requesterDto = userService.add(UserMapper.toUserDto(requester));
        LocalDateTime created = LocalDateTime.parse("2022-10-05T01:00");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, created);
        itemRequestDto = modelMapper.map(itemRequest, ItemRequestDto.class);
        itemRequestListInfoDto = modelMapper.map(itemRequest, ItemRequestListInfoDto.class);
    }

    @Test
    void addRequestTest() {
        itemRequestListInfoDto = requestService.addRequest(requesterDto.getId(), itemRequestDto);
        itemRequestListInfoDto.setItems(new ArrayList<>());
        assertEquals(itemRequestListInfoDto, requestService.findByRequestId(requesterDto.getId(), itemRequestDto.getId()));
    }

    @Test
    void findAllByRequesterTest() {
        itemRequestListInfoDto = requestService.addRequest(requesterDto.getId(), itemRequestDto);
        itemRequestListInfoDto.setItems(new ArrayList<>());
        Collection<ItemRequestListInfoDto> requests = requestService.findAllByRequester(requesterDto.getId());
        assertThat(requests).hasSize(1).contains(itemRequestListInfoDto);
    }

    @Test
    void findRequestsByParamsTest() {
        itemRequestListInfoDto = requestService.addRequest(requesterDto.getId(), itemRequestDto);
        itemRequestListInfoDto.setItems(new ArrayList<>());
        Collection<ItemRequestListInfoDto> requests = requestService.findRequestsByParams(requesterDto.getId(), 0, 2);
        assertThat(requests).hasSize(0);
    }

    @Test
    void findByRequestIdTest() {
        itemRequestListInfoDto = requestService.addRequest(requesterDto.getId(), itemRequestDto);
        itemRequestListInfoDto.setItems(new ArrayList<>());
        assertThat(itemRequestListInfoDto.getId()).isNotZero();
        assertThat(itemRequestListInfoDto.getDescription()).isEqualTo("description");
        assertThat(itemRequestListInfoDto.getItems().size()).isZero();
    }
}
