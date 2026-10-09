package com.example.ksl.wsd2025.api.v3;

import com.example.ksl.wsd2025.api.dto.ItemDto;
import com.example.ksl.wsd2025.api.exception.ApiException;
import com.example.ksl.wsd2025.api.request.PriceUpdateRequest;
import com.example.ksl.wsd2025.api.response.ApiResponse;
import com.example.ksl.wsd2025.api.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/*
v3: v2 + 요청 헤더(@RequestHeader)로 값을 받는 기능
PUT 가격 수정 / DELETE 전체 삭제
*/
@RestController
@RequestMapping("/api/v3/items")
public class ItemController3 {

    private final ItemService service;

    public ItemController3(ItemService service) {
        this.service = service;
    }

    // PUT 2) 가격만 수정 (X-USER-ID 헤더로 수정자 기록) -> 200 / 400 / 404
    @PutMapping("/{id}/price")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updatePrice(
            @PathVariable Long id,
            @Valid @RequestBody PriceUpdateRequest request,
            @RequestHeader(value = "X-USER-ID", required = false) String userId) {
        ItemDto item = service.update(id, null, request.getPrice());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("item", item);
        data.put("updatedBy", userId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // DELETE 2) 전체 삭제 (Authorization 헤더 필수) -> 200 / 401
    @DeleteMapping
    public ResponseEntity<ApiResponse<Map<String, Integer>>> deleteAll(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Authorization 헤더가 필요합니다.");
        }
        return ResponseEntity.ok(ApiResponse.success(Map.of("deletedCount", service.clear())));
    }
}
