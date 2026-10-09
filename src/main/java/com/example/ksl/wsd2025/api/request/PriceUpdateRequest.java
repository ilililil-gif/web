package com.example.ksl.wsd2025.api.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PriceUpdateRequest {
    @NotNull(message = "price는 필수입니다.")
    @Min(value = 0, message = "price는 0 이상이어야 합니다.")
    private Integer price;

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
}
