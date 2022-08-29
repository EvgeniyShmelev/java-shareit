package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utill.NumberGenerator;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final NumberGenerator numberGenerator;


    public Optional<Item> getItemById(long userId, long itemId) {
        if (userId <= 0 & itemId <= 0) {
            log.info("ID равно 0");
            throw new ValidationException("ID меньше или равно 0");
        }
        return itemRepository.findById(itemId);
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        List<ItemDto> allItems = new ArrayList<>();
        ItemDto itemDto;
        if (userId <= 0) {
            log.info("ID пользователя равен 0");
            throw new ValidationException("ID меньше или равно 0");
        }
        List<Item> items = itemRepository.findAll().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
        for (Item item : items) {
            itemDto = ItemMapper.toItemDto(item);
            allItems.add(itemDto);
        }
        allItems = allItems.stream().sorted(comparing(ItemDto::getId)).collect(Collectors.toList());
        return allItems;
    }

    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        if (userId <= 0 || userService.get(userId) == null) {
            throw new ValidationException("ID меньше или равно 0");
        }
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            log.info("Нет наименования вещи");
            throw new ValidationException("Нет наименования вещи");
        }
        if (itemDto.getDescription() == null) {
            log.info("Нет описания вещи");
            throw new ValidationException("Нет описания вещи");
        }
        if (itemDto.getAvailable() == null) {
            log.info("Нет статуса доступности вещи");
            throw new ValidationException("Отсутствует статус доступности вещи");
        }
        Optional<User> owner = userRepository.findById(userId);
        Item item = ItemMapper.toItem(owner.get(), itemDto, null);
        item.setId(numberGenerator.getId());
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        if (userId <= 0) {
            log.info("ID пользователя равно 0");
            throw new ValidationException("ID пользователя меньше или равно 0");
        }
        if (userService.get(userId) == null) {
            log.info("Пользователь {} не существует", userId);
            throw new ValidationException("Пользователь " + userId + " не существует");
        }
        Optional<User> owner = userRepository.findById(userId);
        Item item = ItemMapper.toItem(owner.get(), itemDto, null);
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public void delete(long userId, long itemId) {
        if (userId <= 0) {
            log.info("ID пользователя меньше или равно 0");
            throw new ValidationException("ID пользователя меньше или равно 0");
        }
        if (itemId <= 0) {
            log.info("ID вещи меньше или равно 0");
            throw new ValidationException("ID вещи меньше или равно 0");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> search(long userId, String search) {
        if (userId <= 0) {
            log.info("ID пользователя меньше 0");
            throw new ValidationException("ID пользователя меньше 0");
        }
        if (search.isEmpty()) {
            log.info("Строка поиска пустая");
            return new ArrayList<>();
        }
        return itemRepository.search(search).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}