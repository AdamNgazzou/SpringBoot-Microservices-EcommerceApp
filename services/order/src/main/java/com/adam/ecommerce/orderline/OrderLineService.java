package com.adam.ecommerce.orderline;

import com.adam.ecommerce.order.Order;
import com.adam.ecommerce.order.OrderService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderLineService {

        private final OrderLineRepository repository ;
        private final OrderLineMapper mapper;
        private final EntityManager entityManager;

        public Integer saveOrderLine(OrderLineRequest request) {
            var orderLine = mapper.toOrderLine(request);

            Order orderRef = entityManager.getReference(Order.class, request.orderId());
            orderLine.setOrder(orderRef);

            return repository.save(orderLine).getId();
        }

    public List<OrderLineResponse> findAllBydOrderId(Integer orderId) {
        return repository.findAllByOrderId(orderId)
                .stream()
                .map(mapper::toOrderlineResponse)
                .collect(Collectors.toList())
                ;
    }
    
}
