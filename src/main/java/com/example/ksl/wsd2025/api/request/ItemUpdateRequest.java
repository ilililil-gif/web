package com.example.ksl.wsd2025.api.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public class ItemUpdateRequest {
    @Pattern(regexp = ".*\\S.*", message = "name은 공백일 수 없습니다.")
    private String name;

    @Min(value = 0, message = "price는 0 이상이어야 합니다.")
    private Integer price;

    // getter / setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
}
