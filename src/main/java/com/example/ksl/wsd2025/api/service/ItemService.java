package com.example.ksl.wsd2025.api.service;

import com.example.ksl.wsd2025.api.dto.ItemDto;
import com.example.ksl.wsd2025.api.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

/** 메모리용 임시 DB. v1·v2·v3 컨트롤러가 하나의 빈을 공유한다. */
@Service
public class ItemService {

    private final Map<Long, ItemDto> store = new ConcurrentSkipListMap<>();
    private final AtomicLong sequence = new AtomicLong(1L);

    public synchronized ItemDto create(String name, Integer price) {
        if (store.values().stream().anyMatch(i -> i.getName().equals(name))) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 존재하는 name 입니다: " + name);
        }
        ItemDto item = new ItemDto();
        item.setId(sequence.getAndIncrement());
        item.setName(name);
        item.setPrice(price);
        store.put(item.getId(), item);
        return item;
    }

    public List<ItemDto> findAll() {
        return new ArrayList<>(store.values());
    }

    public ItemDto get(Long id) {
        ItemDto item = store.get(id);
        if (item == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "id=" + id + " 아이템을 찾을 수 없습니다.");
        }
        return item;
    }

    public List<ItemDto> search(String keyword, int page, int size) {
        return store.values().stream()
                .filter(i -> keyword == null || i.getName().contains(keyword))
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    public ItemDto update(Long id, String name, Integer price) {
        ItemDto item = get(id);
        if (name != null) item.setName(name);
        if (price != null) item.setPrice(price);
        return item;
    }

    public ItemDto remove(Long id) {
        ItemDto removed = store.remove(id);
        if (removed == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "id=" + id + " 아이템을 찾을 수 없습니다.");
        }
        return removed;
    }

    public int clear() {
        int n = store.size();
        store.clear();
        return n;
    }

    /** 장애 시뮬레이션: 500 / 503 응답 확인용 */
    public static void simulate(String type) {
        if (type == null) return;
        switch (type) {
            case "500" -> throw new IllegalStateException("simulated internal error");
            case "503" -> throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "서비스를 일시적으로 사용할 수 없습니다.");
            default -> throw new ApiException(HttpStatus.BAD_REQUEST, "simulate는 500 또는 503만 가능합니다.");
        }
    }
}
