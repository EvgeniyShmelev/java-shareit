package ru.practicum.shareit.utill;

import org.springframework.context.annotation.Scope;

@Scope("prototype")
public class NumberGenerator {
    private long id = 1;

    public long getId() {
        return id++;
    }

}
