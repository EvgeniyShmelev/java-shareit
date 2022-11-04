package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class BookingAddDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
