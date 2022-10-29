package ru.practicum.shareit.utill;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class NumberGenerator {
    private long id = 1;

    public long getId() {
        return id++;
    }

}
