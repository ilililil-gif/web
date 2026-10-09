package com.example.ksl.wsd2025.api.v1;

import com.example.ksl.wsd2025.api.dto.ItemDto;
import com.example.ksl.wsd2025.api.request.ItemCreateRequest;
import com.example.ksl.wsd2025.api.request.ItemUpdateRequest;
import com.example.ksl.wsd2025.api.response.ApiResponse;
import com.example.ksl.wsd2025.api.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
v1: 경로(@PathVariable)와 본문(@RequestBody)으로 값을 받는 기본 CRUD
POST 생성 / GET 단건 조회 / PUT 수정 / DELETE 단건 삭제
*/
@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    // POST 1) 생성 -> 201 / 400 / 409
    @PostMapping
    public ResponseEntity<ApiResponse<ItemDto>> createItem(@Valid @RequestBody ItemCreateRequest request) {
        ItemDto item = service.create(request.getName(), request.getPrice());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(item));
    }

    // GET) 전체 조회 -> 200
    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemDto>>> getItems() {
        return ResponseEntity.ok(ApiResponse.success(service.findAll()));
    }

    // GET 1) 단건 조회 (PathVariable) -> 200 / 404 / 400 (id가 숫자가 아님)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemDto>> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.get(id)));
    }

    // PUT 1) 수정 -> 200 / 400 / 404
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemDto>> updateItem(
            @PathVariable Long id, @Valid @RequestBody ItemUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, request.getName(), request.getPrice())));
    }

    // DELETE 1) 단건 삭제 -> 200 / 404
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemDto>> deleteItem(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.remove(id), "삭제되었습니다."));
    }
}
