package com.example.ksl.wsd2025.api.v2;

import com.example.ksl.wsd2025.api.dto.ItemDto;
import com.example.ksl.wsd2025.api.exception.ApiException;
import com.example.ksl.wsd2025.api.request.ItemCreateRequest;
import com.example.ksl.wsd2025.api.response.ApiResponse;
import com.example.ksl.wsd2025.api.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
v2: v1 + 쿼리 파라미터(@RequestParam)로 값을 받는 기능
GET 검색·페이징 / POST 일괄 생성
*/
@RestController
@RequestMapping("/api/v2/items")
public class ItemController2 {

    private final ItemService service;

    public ItemController2(ItemService service) {
        this.service = service;
    }

    // GET 2) 검색 + 페이징 (RequestParam) -> 200 / 400
    // /api/v2/items?keyword=abc&page=0&size=10  (?simulate=500|503 으로 서버 오류 확인)
    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemDto>>> searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String simulate) {
        ItemService.simulate(simulate);
        if (page < 0 || size < 1) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "page는 0 이상, size는 1 이상이어야 합니다.");
        }
        return ResponseEntity.ok(ApiResponse.success(service.search(keyword, page, size)));
    }

    // POST 2) 일괄 생성 -> 201 / 400 (빈 목록, 검증 실패) / 409
    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<ItemDto>>> createItems(
            @Valid @RequestBody List<@Valid ItemCreateRequest> requests) {
        if (requests.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "생성할 아이템 목록이 비어 있습니다.");
        }
        List<ItemDto> created = requests.stream()
                .map(r -> service.create(r.getName(), r.getPrice()))
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }
}
