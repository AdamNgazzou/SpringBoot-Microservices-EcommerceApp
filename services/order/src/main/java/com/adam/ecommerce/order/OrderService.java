package com.adam.ecommerce.order;

import com.adam.ecommerce.customer.CustomerClient;
import com.adam.ecommerce.exception.BusinessException;
import com.adam.ecommerce.kafka.OrderConfirmation;
import com.adam.ecommerce.kafka.OrderProducer;
import com.adam.ecommerce.orderline.OrderLineRequest;
import com.adam.ecommerce.orderline.OrderLineService;
import com.adam.ecommerce.payment.PaymentClient;
import com.adam.ecommerce.payment.PaymentRequest;
import com.adam.ecommerce.product.ProductClient;
import com.adam.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer createdOrder(@Valid OrderRequest request) {
        // check the customer --> OpenFeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("cannot create Order :: Customer not found with provided ID"));

        // Purchase the products --> Product-ms (RestTemplate)
        var purchasedProducts = this.productClient.purchaseProducts(request.products());

        // persist Order
        var order = this.repository.save(mapper.toOrder(request));
        // persist Order lines (saving)
        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        // send the order confirmation --> notification-ms (kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );
        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return repository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Order with id %s not found", orderId)));
    }
    
}
