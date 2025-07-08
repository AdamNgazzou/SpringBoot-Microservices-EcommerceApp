package com.adam.ecommerce.kafka;

import com.adam.ecommerce.customer.CustomerResponse;
import com.adam.ecommerce.order.PaymentMethod;
import com.adam.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
