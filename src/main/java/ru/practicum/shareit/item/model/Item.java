package ru.practicum.shareit.item.model;

import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Класс вещи для аренды
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;            //уникальный идентификатор вещи;

    private String name;        //краткое название;

    private String description; //развёрнутое описание;

    @Column(name = "is_available")
    private Boolean available;  //статус о том, доступна или нет вещь для аренды;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;         //владелец вещи;

    @Column(name = "request_id")
    private Long request; /*если вещь была создана по запросу другого пользователя,
     то в этом поле будет храниться
    ссылка на соответствующий запрос.*/

    @Transient
    private Set<Comment> comment = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Item item = (Item) o;
        return id != null && Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}