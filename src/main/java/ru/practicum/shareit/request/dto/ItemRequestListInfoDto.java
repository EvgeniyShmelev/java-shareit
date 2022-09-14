package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.item.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
public class ItemRequestListInfoDto {

    private long id;
    @NotBlank(groups = Create.class)
    private String description;
    private LocalDateTime created;
    private Collection<ItemDto> items;
}
