package com.adam.ecommerce.customer;

import com.adam.ecommerce.exception.CustomerNotFoundException;
import com.adam.ecommerce.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        CustomerController customerController = new CustomerController(customerService);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testCreateCustomer() throws Exception {
        // Given
        String customerId = "customer123";
        when(customerService.createCustomer(any(CustomerRequest.class))).thenReturn(customerId);

        // When & Then
        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(customerId));
    }

    @Test
    public void testCreateCustomer_InvalidRequest() throws Exception {
        // When & Then - Missing required fields
        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"\",\"lastName\":\"\"}"))
                .andExpect(status().isNotFound()); // GlobalExceptionHandler returns NOT_FOUND for validation errors
    }

    @Test
    public void testFindAllCustomers() throws Exception {
        // Given
        Address address1 = Address.builder()
                .street("Main St")
                .houseNumber("123")
                .zipCode("12345")
                .build();
        Address address2 = Address.builder()
                .street("Second St")
                .houseNumber("456")
                .zipCode("67890")
                .build();

        List<CustomerResponse> customers = Arrays.asList(
                new CustomerResponse("1", "John", "Doe", "john.doe@example.com", address1),
                new CustomerResponse("2", "Jane", "Smith", "jane.smith@example.com", address2)
        );
        when(customerService.findAllCustomers()).thenReturn(customers);

        // When & Then
        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    public void testFindAllCustomers_EmptyList() throws Exception {
        // Given
        when(customerService.findAllCustomers()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testFindCustomerById() throws Exception {
        // Given
        String customerId = "customer123";
        Address address = Address.builder()
                .street("Main St")
                .houseNumber("123")
                .zipCode("12345")
                .build();
        CustomerResponse customer = new CustomerResponse(customerId, "John", "Doe", "john.doe@example.com", address);
        when(customerService.findById(customerId)).thenReturn(customer);

        // When & Then
        mockMvc.perform(get("/api/v1/customers/exists/" + customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    public void testFindCustomerById_NotFound() throws Exception {
        // Given
        String customerId = "nonexistent";
        CustomerNotFoundException exception = new CustomerNotFoundException("Customer with id nonexistent not found");
        when(customerService.findById(customerId)).thenThrow(exception);

        // When & Then
        mockMvc.perform(get("/api/v1/customers/exists/" + customerId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer with id nonexistent not found"));
    }

    @Test
    public void testUpdateCustomer() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"customer123\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\"}"))
                .andExpect(status().isAccepted());
    }

    @Test
    public void testUpdateCustomer_CustomerNotFound() throws Exception {
        // Given
        CustomerNotFoundException exception = new CustomerNotFoundException("Customer with id nonexistent not found");
        doThrow(exception).when(customerService).updateCustomer(any(CustomerRequest.class));

        // When & Then
        mockMvc.perform(put("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"nonexistent\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer with id nonexistent not found"));
    }

    @Test
    public void testUpdateCustomer_InvalidRequest() throws Exception {
        // When & Then - Missing required fields
        mockMvc.perform(put("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"customer123\",\"firstName\":\"\",\"lastName\":\"\"}"))
                .andExpect(status().isNotFound()); // GlobalExceptionHandler returns NOT_FOUND for validation errors
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        // Given
        String customerId = "customer123";

        // When & Then
        mockMvc.perform(delete("/api/v1/customers/exists/" + customerId))
                .andExpect(status().isAccepted());
    }

    @Test
    public void testDeleteCustomer_NonExistentId() throws Exception {
        // Given
        String customerId = "nonexistent";
        doThrow(new CustomerNotFoundException("Customer with id nonexistent not found"))
            .when(customerService).deleteCustomer(customerId);

        // When & Then
        mockMvc.perform(delete("/api/v1/customers/exists/" + customerId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer with id nonexistent not found"));
    }
}
