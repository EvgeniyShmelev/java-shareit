package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private final User user = new User(1L, "Evgeniy", "Evgeniy@mail.ru");
    private final UserDto userDto = UserMapper.toUserDto(user);
    private final Collection<UserDto> allUsers = List.of(userDto);

    @Test
    void shouldReturnUsersTest() throws Exception {
        when(userService.get(any()))
                .thenReturn(userDto);

        mvc.perform(get("/users/{userId}", 1L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void shouldAddUserTest() throws Exception {
        when(userService.add(any())).thenReturn(userDto);
        mvc.perform(
                        post("/users")
                                .content(mapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Evgeniy"))
                .andExpect(jsonPath("$.email").value("Evgeniy@mail.ru"));
    }

    @Test
    void shouldUpdateUserTest() throws Exception {
        when(userService.update(anyLong(), any())).thenReturn(userDto);

        mvc.perform(
                        patch("/users/{userId}", 1)
                                .content(mapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    public void shouldReturnAllUsersTest() throws Exception {
        when(userService.getUsers())
                .thenReturn(allUsers);
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(allUsers.size()));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        mvc.perform(delete("/users/1")
                        .param("id", "1"))
                .andExpect(status().isOk());
    }
}
