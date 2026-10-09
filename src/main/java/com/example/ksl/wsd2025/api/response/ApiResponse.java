package com.example.ksl.wsd2025.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 표준 응답 포맷
 * 성공: { "status": "success", "data": {...} }
 * 실패: { "status": "error", "code": 404, "message": "..." }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final String status;
    private final T data;
    private final Integer code;
    private final String message;

    private ApiResponse(String status, T data, Integer code, String message) {
        this.status = status;
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", data, null, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>("success", data, null, message);
    }

    public static ApiResponse<Void> error(int code, String message) {
        return new ApiResponse<>("error", null, code, message);
    }

    public String getStatus() { return status; }
    public T getData() { return data; }
    public Integer getCode() { return code; }
    public String getMessage() { return message; }
}
