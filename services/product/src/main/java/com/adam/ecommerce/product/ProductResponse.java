package com.adam.ecommerce.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductResponse(
        Integer id,
        String name,
        String description,
        double available_quantity,
        BigDecimal price,

        Integer categoryId ,
        String categoryName,
        String categoryDescription
) {
}
