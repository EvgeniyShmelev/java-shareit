package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Класс отображения для запроса вещи
 */
@Data
public class ItemRequestDto {
    private Long id;            //уникальный идентификатор запроса;
    private String description; //текст запроса, содержащий описание требуемой вещи;
    private LocalDateTime created;       //дата и время создания запроса.

}