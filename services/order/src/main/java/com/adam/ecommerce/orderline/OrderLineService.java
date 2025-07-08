package com.adam.ecommerce.orderline;

import com.adam.ecommerce.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderLineService {

        private final OrderLineRepository repository ;
        private final OrderLineMapper mapper;
        public Integer saveOrderLine(OrderLineRequest request) {
            var order = mapper.toOrderLine(request);
            return repository.save(order).getId();
        }

    public List<OrderLineResponse> findAllBydOrderId(Integer orderId) {
        return repository.findAllByOrderId(orderId)
                .stream()
                .map(mapper::toOrderlineResponse)
                .collect(Collectors.toList())
                ;
    }
}
