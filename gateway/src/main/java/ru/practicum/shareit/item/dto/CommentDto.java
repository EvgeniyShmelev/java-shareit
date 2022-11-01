package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private long id;
    @NotBlank(groups = Create.class)
    private String text;
    private Long authorId;
    private String authorName;
    private LocalDateTime created;
}