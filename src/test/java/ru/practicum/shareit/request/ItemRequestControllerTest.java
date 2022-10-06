package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestListInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @MockBean
    private RequestService requestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ModelMapper modelMapper;
    ItemRequest itemRequest1;
    ItemRequest itemRequest2;
    ItemRequestDto itemRequestDto1;
    ItemRequestDto itemRequestDto2;
    ItemRequestListInfoDto itemRequestListInfoDto1;
    ItemRequestListInfoDto itemRequestListInfoDto2;


    @BeforeEach
    void createRequests() {
        LocalDateTime created1 = LocalDateTime.parse("2022-10-05T02:00");
        LocalDateTime created2 = LocalDateTime.parse("2022-10-05T01:00");
        itemRequest1 = new ItemRequest(1L, "description1", null, created1);
        itemRequest2 = new ItemRequest(2L, "description2", null, created2);
        itemRequestDto1 = modelMapper.map(itemRequest1, ItemRequestDto.class);
        itemRequestDto2 = modelMapper.map(itemRequest2, ItemRequestDto.class);
        itemRequestListInfoDto1 = modelMapper.map(itemRequest1, ItemRequestListInfoDto.class);
        itemRequestListInfoDto2 = modelMapper.map(itemRequest2, ItemRequestListInfoDto.class);
    }

    @Test
    void addRequestTset() throws Exception {
        when(requestService.addRequest(anyLong(), any())).thenReturn(itemRequestListInfoDto1);

        mockMvc.perform(
                        post("/requests")
                                .content(mapper.writeValueAsString(itemRequestListInfoDto1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.description").value("description1"));
    }

    @Test
    void findAllByRequesterTest() throws Exception {
        Collection<ItemRequestListInfoDto> requests = List.of(itemRequestListInfoDto1);
        when(requestService.findAllByRequester(anyLong())).thenReturn(requests);

        mockMvc.perform(
                        get("/requests")
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(requests.size()));
    }

    @Test
    void findRequestsByParamsTest() throws Exception {
        Collection<ItemRequestListInfoDto> requests = List.of(itemRequestListInfoDto1, itemRequestListInfoDto2);
        when(requestService.findRequestsByParams(anyLong(), anyInt(), anyInt())).thenReturn(requests);

        mockMvc.perform(
                        get("/requests/all")
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(requests.size()));
    }

    @Test
    void findByRequestIdTest() throws Exception {
        when(requestService.findByRequestId(anyLong(), anyLong())).thenReturn(itemRequestListInfoDto1);

        mockMvc.perform(
                        get("/requests/{requestId}", 1L)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.description").value("description1"));
    }
}
