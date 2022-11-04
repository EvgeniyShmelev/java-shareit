package ru.practicum.shareit.item.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private long id;
    private String text;
    private Long authorId;
    private String authorName;
    private LocalDateTime created;
}