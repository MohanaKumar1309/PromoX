package com.cts.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CheckoutRequest {
    @NotEmpty
    private List<CartLine> items;
    private String couponCode;

    @Data
    public static class CartLine {
        private Long productId;
        private Integer quantity;
    }
}
