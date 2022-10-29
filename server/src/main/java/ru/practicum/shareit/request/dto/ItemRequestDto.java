package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * Класс отображения для запроса вещи
 */
@Data

public class ItemRequestDto {
    private Long id;            //уникальный идентификатор запроса;

    @NotBlank(groups = Create.class)
    private String description; //текст запроса, содержащий описание требуемой вещи;
    private LocalDateTime created;       //дата и время создания запроса.

}