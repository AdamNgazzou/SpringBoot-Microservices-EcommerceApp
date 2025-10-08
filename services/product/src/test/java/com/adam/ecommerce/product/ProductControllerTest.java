package com.adam.ecommerce.product;

import com.adam.ecommerce.exception.ProductPurchaseException;
import com.adam.ecommerce.handler.GlobalExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ProductController productController = new ProductController(productService);
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testCreateProduct() throws Exception {
        // Given
        Integer productId = 1;
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(productId);

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Product\",\"description\":\"Test Description\",\"available_quantity\":10,\"price\":99.99,\"categoryId\":1}"))
                .andExpect(status().isOk())
                .andExpect(content().string(productId.toString()));
    }

    @Test
    public void testCreateProduct_InvalidRequest() throws Exception {
        // When & Then - Missing required fields
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"description\":\"\"}"))
                .andExpect(status().isNotFound()); // GlobalExceptionHandler returns NOT_FOUND for validation errors
    }

    @Test
    public void testFindProductById() throws Exception {
        // Given
        Integer productId = 1;
        ProductResponse product = new ProductResponse(
                productId,
                "Test Product",
                "Test Description",
                10.0,
                new BigDecimal("99.99"),
                1,
                "Test Category",
                "Test Category Description"
        );
        when(productService.findById(productId)).thenReturn(product);

        // When & Then
        mockMvc.perform(get("/api/v1/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    public void testFindProductById_NotFound() throws Exception {
        // Given
        Integer productId = 999;
        when(productService.findById(productId)).thenThrow(new EntityNotFoundException("Product not found with the ID:: " + productId));

        // When & Then
        mockMvc.perform(get("/api/v1/products/" + productId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Product not found with the ID:: " + productId));
    }

    @Test
    public void testFindAllProducts() throws Exception {
        // Given
        List<ProductResponse> products = Arrays.asList(
                new ProductResponse(1, "Product 1", "Description 1", 10.0, new BigDecimal("99.99"), 1, "Category 1", "Category 1 Description"),
                new ProductResponse(2, "Product 2", "Description 2", 20.0, new BigDecimal("199.99"), 2, "Category 2", "Category 2 Description")
        );
        when(productService.findAll()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Product 2"));
    }

    @Test
    public void testFindAllProducts_EmptyList() throws Exception {
        // Given
        when(productService.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testPurchaseProducts_Success() throws Exception, ProductPurchaseException {
        // Given
        List<ProductPurchaseResponse> responses = Arrays.asList(
                new ProductPurchaseResponse(1, "Product 1", "Description 1", new BigDecimal("99.99"), 5),
                new ProductPurchaseResponse(2, "Product 2", "Description 2", new BigDecimal("199.99"), 3)
        );

        when(productService.purchaseProducts(anyList())).thenReturn(responses);

        // When & Then
        mockMvc.perform(post("/api/v1/products/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"productId\":1,\"quantity\":5},{\"productId\":2,\"quantity\":3}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[0].quantity").value(5))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Product 2"))
                .andExpect(jsonPath("$[1].quantity").value(3));
    }

    @Test
    public void testPurchaseProducts_ProductNotFound() throws Exception, ProductPurchaseException {
        // Given
        when(productService.purchaseProducts(anyList()))
                .thenThrow(new ProductPurchaseException("One or more products does not exists"));

        // When & Then
        mockMvc.perform(post("/api/v1/products/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"productId\":999,\"quantity\":5}]"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("One or more products does not exists"));
    }

    @Test
    public void testPurchaseProducts_InsufficientQuantity() throws Exception, ProductPurchaseException {
        // Given
        when(productService.purchaseProducts(anyList()))
                .thenThrow(new ProductPurchaseException("Not enough quantity of product with id:: 1"));

        // When & Then
        mockMvc.perform(post("/api/v1/products/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"productId\":1,\"quantity\":1000}]"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not enough quantity of product with id:: 1"));
    }

    @Test
    public void testPurchaseProducts_InvalidRequest() throws Exception {
        // When & Then - Missing required fields
        mockMvc.perform(post("/api/v1/products/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"quantity\":5}]")) // Missing productId
                .andExpect(status().isBadRequest());
    }
}
