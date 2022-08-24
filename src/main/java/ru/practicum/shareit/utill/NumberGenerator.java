package ru.practicum.shareit.utill;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class NumberGenerator {
    private long userId = 1;
    private long itemId = 1;
    private long itemRequestId = 1;
    private long bookingId = 1;

    public long getUserId() {
        return userId++;
    }

    public long getItemId() {
        return itemId++;
    }

    public long getItemRequestId() {
        return itemRequestId++;
    }

    public long getBookingId() {
        return bookingId++;
    }
}
