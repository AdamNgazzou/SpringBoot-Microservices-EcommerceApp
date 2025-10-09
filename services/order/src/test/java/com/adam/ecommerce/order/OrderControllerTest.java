package com.adam.ecommerce.order;

import com.adam.ecommerce.product.PurchaseRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("Should create order and return order ID")
    void createOrder() throws Exception {
        Mockito.when(orderService.createdOrder(any(OrderRequest.class))).thenReturn(1);

        OrderRequest request = new OrderRequest(
                null,
                "ORDER-001",
                BigDecimal.valueOf(100.0),
                PaymentMethod.CREDIT_CARD,
                "customer123",
                List.of(new PurchaseRequest(1, 2))
        );

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("Should return all orders")
    void findAll() throws Exception {
        Mockito.when(orderService.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("Should return order by ID")
    void findById() throws Exception {
        OrderResponse response = new OrderResponse(
                1,                          // id
                "ORDER-001",                // reference
                BigDecimal.valueOf(100.0),  // amount
                PaymentMethod.CREDIT_CARD,  // paymentMethod
                "customer123"               // customerId
        );

        Mockito.when(orderService.findById(1)).thenReturn(response);
        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isOk());
    }
}
