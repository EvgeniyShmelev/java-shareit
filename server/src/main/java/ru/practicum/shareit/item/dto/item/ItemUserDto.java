package ru.practicum.shareit.item.dto.item;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;

import java.util.Collection;

@Data
public class ItemUserDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId; //в этом поле будет храниться ссылка на соответствующий запрос
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private Collection<CommentDto> comments;
}
