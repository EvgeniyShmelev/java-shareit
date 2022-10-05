package ru.practicum.shareit.bookings;

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
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelMapper modelMapper;
    private BookingDto bookingDto;
    private BookingAddDto bookingAddDto;
    private final LocalDateTime start = LocalDateTime.parse("2022-10-01T01:00:00");
    private final LocalDateTime end = LocalDateTime.parse("2052-10-01T01:00:00");

    @BeforeEach
    void beforeEach() {
        User owner = new User(1L, "owner", "owner@gmail.com");
        User booker = new User(2L, "booker", "booker@gmail.com");
        Item item = new Item(1L, "item", "description", true, owner, null);
        Booking booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);
        bookingDto = modelMapper.map(booking, BookingDto.class);
        bookingAddDto = modelMapper.map(booking, BookingAddDto.class);
    }

    @Test
    void addBookingTest() throws Exception {
        when(bookingService.add(1L, bookingAddDto))
                .thenReturn(bookingDto);

        mockMvc.perform(
                        post("/bookings")
                                .content(mapper.writeValueAsString(bookingAddDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.start").value("2022-10-01T01:00:00"))
                .andExpect(jsonPath("$.end").value("2052-10-01T01:00:00"))
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.toString()));
    }

    @Test
    void updateBookingTest() throws Exception {
        bookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.update(1L, 1L, "true")).thenReturn(bookingDto);

        mockMvc.perform(
                        patch("/bookings/{bookingId}", 1L)
                                .param("approved", "true")
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.start").value("2022-10-01T01:00:00"))
                .andExpect(jsonPath("$.end").value("2052-10-01T01:00:00"))
                .andExpect(jsonPath("$.status").value(BookingStatus.APPROVED.toString()));
    }

    @Test
    void getBookingByIdTest() throws Exception {
        when(bookingService.getById(1L, 1L)).thenReturn(bookingDto);

        mockMvc.perform(
                        get("/bookings/{bookingId}", 1L)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.start").value("2022-10-01T01:00:00"))
                .andExpect(jsonPath("$.end").value("2052-10-01T01:00:00"))
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.toString()));
    }

    @Test
    void getBookingsByUserTest() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);
        when(bookingService.getBookingsByUser(anyLong(), any(), anyInt(), anyInt())).thenReturn(bookings);

        mockMvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(bookings.size()));
    }

    @Test
    void getBookingsByOwnerTest() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);
        when(bookingService.getBookingByOwner(anyLong(), any(), anyInt(), anyInt())).thenReturn(bookings);

        mockMvc.perform(
                        get("/bookings/owner")
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(bookings.size()));
    }

    @Test
    void deleteBookingTest() throws Exception {
        mockMvc.perform(delete("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("id", "1"))
                .andExpect(status().isOk());
    }
}
