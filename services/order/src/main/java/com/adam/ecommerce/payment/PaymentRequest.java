package com.adam.ecommerce.payment;

import com.adam.ecommerce.customer.CustomerResponse;
import com.adam.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
