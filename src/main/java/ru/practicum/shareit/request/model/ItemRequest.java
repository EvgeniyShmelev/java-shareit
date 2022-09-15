package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс запроса для бронирования
 */
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            //уникальный идентификатор запроса;

    @Column(name = "description")
    @Size(max = 512)
    private String description; //текст запроса, содержащий описание требуемой вещи;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;     //пользователь, создавший запрос;
    
    private LocalDateTime created;       //дата и время создания запроса.


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        ItemRequest that = (ItemRequest) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}