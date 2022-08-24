package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {

    private long id;
    @NotBlank(groups = ItemCreate.class)
    private String name;
    @NotBlank(groups = ItemCreate.class)
    private String description;
    @NotNull(groups = ItemCreate.class)
    private Boolean available;
    private Long requestId; //в этом поле будет храниться ссылка на соответствующий запрос

}