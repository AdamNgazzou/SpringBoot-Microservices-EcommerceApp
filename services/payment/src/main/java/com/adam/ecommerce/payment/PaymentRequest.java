package com.adam.ecommerce.payment;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
    Integer id,
    @NotNull BigDecimal amount,
    @NotNull PaymentMethod paymentMethod,
    @NotNull Integer orderId,
    @NotNull String orderReference,
    @NotNull Customer customer
) {}
    