package com.adam.ecommerce.order;

import com.adam.ecommerce.product.PurchaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        Integer id,
        String reference,
        @Positive(message="Order amount must be positive")
        BigDecimal amount,
        @NotNull(message="Order payment method is required")
        PaymentMethod paymentMethod,
        @NotNull(message="Order customer id is required")
        @NotEmpty(message="Order customer id is required")
        @NotBlank(message="Order customer id is required")
        String customerId,
        @NotEmpty(message="You should at least purchase one product")
        List<PurchaseRequest> products
) {

}
