package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    Collection<ItemRequest> findByRequester_Id(Long requestId, Sort sort);

    Page<ItemRequest> findByRequester_IdIsNot(Long userId, Pageable pageable);


}
