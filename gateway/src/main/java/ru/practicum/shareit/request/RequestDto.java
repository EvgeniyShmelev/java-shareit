package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;
@Data
public class RequestDto {
    @NotBlank(groups = Create.class)
    private String description;
}
