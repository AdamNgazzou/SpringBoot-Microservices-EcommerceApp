package com.adam.ecommerce.product;

import jakarta.validation.constraints.NotNull;

public record ProductPurchaseRequest(
        @NotNull(message="Product id is required")
        Integer productId,
        @NotNull(message="quantity is required")
        double quantity
) {
}
