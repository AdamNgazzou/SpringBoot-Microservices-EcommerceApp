package com.adam.ecommerce.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create payment and return payment ID")
    void createPayment() throws Exception {
        PaymentRequest request = new PaymentRequest(
                1, // id
                new java.math.BigDecimal("100.00"), // amount
                PaymentMethod.CREDIT_CARD, // payment method
                2, // orderId
                "Test payment", // description
                new Customer(
                    1,                // id as String
                    "John",             // firstName
                    "Doe",              // lastName
                    "john@example.com"// email
                )
        );
        Mockito.when(paymentService.createPayment(any(PaymentRequest.class))).thenReturn(1);

        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request for invalid payment request")
    void createPayment_invalidRequest() throws Exception {
        // For example, missing required fields (amount is null)
        PaymentRequest request = new PaymentRequest(
                1,
                null,
                PaymentMethod.CREDIT_CARD,
                2,
                "Test payment",
                new Customer(
                        1,                // id as String
                        "John",             // firstName
                        "Doe",              // lastName
                        "john@example.com"// email
                )
        );

        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 500 Internal Server Error if service throws exception")
    void createPayment_serviceThrowsException() throws Exception {
        PaymentRequest request = new PaymentRequest(
                1,
                new java.math.BigDecimal("100.00"),
                PaymentMethod.CREDIT_CARD,
                2,
                "Test payment",
                new Customer(
                    1,                // id as String
                    "John",             // firstName
                    "Doe",              // lastName
                    "john@example.com"// email
                )
        );
        Mockito.when(paymentService.createPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}
