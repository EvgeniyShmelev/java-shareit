package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    Collection<ItemRequest> findByRequesterId(Long requestId, Sort sort);
}
