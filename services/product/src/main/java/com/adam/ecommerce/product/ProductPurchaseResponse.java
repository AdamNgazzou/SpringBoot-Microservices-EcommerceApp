package com.adam.ecommerce.product;

import java.math.BigDecimal;

public record ProductPurchaseResponse(Integer id, String name, String description, BigDecimal price, double quantity) {
    public record toProductPurchaseResponse(
            Integer productId,
            String name,
            String description,
            BigDecimal price,
            double quantity) {
    }
}
