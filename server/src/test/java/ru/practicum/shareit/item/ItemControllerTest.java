package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentMapper;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemMapper;
import ru.practicum.shareit.item.dto.item.ItemUserDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mockMvc;
    private ItemDto itemDto;
    private ItemUserDto itemUserDto;
    private CommentDto commentDto;

    @BeforeEach
    void beforeEach() {
        User user = new User(1L, "Evgeniy", "user1@email.com");
        Item item = new Item(1L, "item", "description", true, null, null);
        itemDto = ItemMapper.toItemDto(item);
        itemUserDto = ItemMapper.toItemUserDto(item);
        Comment comment = new Comment(1L, "comment", item, user, LocalDateTime.now());
        commentDto = CommentMapper.toDto(comment);
    }

    @Test
    void addItemTest() throws Exception {
        when(itemService.add(any(), any()))
                .thenReturn(itemUserDto);

        mockMvc.perform(
                        post("/items")
                                .content(mapper.writeValueAsString(itemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));

    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(itemUserDto);

        mockMvc.perform(
                        patch("/items/{itemId}", itemUserDto.getId())
                                .content(mapper.writeValueAsString(itemUserDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(itemUserDto.getName()))
                .andExpect(jsonPath("$.description").value(itemUserDto.getDescription()));
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getItemDtoById(anyLong(), anyLong()))
                .thenReturn(itemUserDto);
        mockMvc.perform(
                        get("/items/{itemId}", 1L)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(itemUserDto.getName()))
                .andExpect(jsonPath("$.description").value(itemUserDto.getDescription()));
    }

    @Test
    void getItemsTest() throws Exception {
        Collection<ItemDto> items = List.of(itemDto);
        when(itemService.getItems(anyLong()))
                .thenReturn(items.toArray());
        mockMvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(items.size()));
    }

    @Test
    void searchTest() throws Exception {
        Collection<ItemDto> items = List.of(itemDto);
        when(itemService.search(anyLong(), any()))
                .thenReturn(items);
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "item")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("item")));
    }

    @Test
    void deleteItemTest() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void addCommentToItem() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mockMvc.perform(
                        post("/items/{itemId}/comment", 1L)
                                .content(mapper.writeValueAsString(commentDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));
    }
}
