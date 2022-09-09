package ru.practicum.shareit.item.dto.item;

import lombok.Data;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
public class ItemDto {

    private long id;
    @NotBlank(groups = Create.class)
    private String name;
    @NotBlank(groups = Create.class)
    private String description;
    @NotNull(groups = Create.class)
    private Boolean available;
    private Long requestId; //в этом поле будет храниться ссылка на соответствующий запрос
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private Collection<CommentDto> comments;

}