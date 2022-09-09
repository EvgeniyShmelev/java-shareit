package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class BookingAddDto {
        private Long id;
        @Future(groups = Create.class)
        @NotBlank(groups = LocalDateTime.class)
        private LocalDateTime start;
        @Future(groups = Create.class)
        @NotBlank(groups = LocalDateTime.class)
        private LocalDateTime end;
        private Long itemId;
}
