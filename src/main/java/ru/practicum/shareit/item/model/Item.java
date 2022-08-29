package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;
import javax.persistence.*;

/**
 * Класс вещи для аренды
 */
@Data
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;            //уникальный идентификатор вещи;

    @Column(name = "name")
    private String name;        //краткое название;

    @Column(name = "description")
    private String description; //развёрнутое описание;

    @Column(name = "is_available")
    private Boolean available;  //статус о том, доступна или нет вещь для аренды;

    @Column(name = "owner_id")
    private User owner;         //владелец вещи;

    @Column(name = "request_id")
    private ItemRequest request; /*если вещь была создана по запросу другого пользователя,
     то в этом поле будет храниться
    ссылка на соответствующий запрос.*/
    public Item() {
    }
}