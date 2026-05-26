package com.nyxn.ecommerce.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyxn.ecommerce.application.port.in.ProductUseCase;
import com.nyxn.ecommerce.domain.exception.ProductNotFoundException;
import com.nyxn.ecommerce.domain.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController — Integration Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductUseCase productUseCase;

    private final Product sample = new Product(1L, "Laptop HP", "Laptop empresarial",
            new BigDecimal("1299.99"), 50, "Electrónica", LocalDateTime.now());

    @Test
    @DisplayName("GET /products — 200 OK con página de productos")
    void getAllProducts_returns200() throws Exception {
        when(productUseCase.getAllProducts(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sample)));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Laptop HP"))
                .andExpect(jsonPath("$.content[0].price").value(1299.99));
    }

    @Test
    @DisplayName("GET /products/{id} — 200 OK cuando el producto existe")
    void getById_exists_returns200() throws Exception {
        when(productUseCase.getProductById(1L)).thenReturn(sample);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.category").value("Electrónica"));
    }

    @Test
    @DisplayName("GET /products/{id} — 404 cuando el producto no existe")
    void getById_notExists_returns404() throws Exception {
        when(productUseCase.getProductById(99L))
                .thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(get("/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("POST /products — 201 Created con datos válidos")
    void createProduct_validData_returns201() throws Exception {
        when(productUseCase.createProduct(any())).thenReturn(sample);

        var body = Map.of(
                "name", "Laptop HP",
                "description", "Laptop empresarial",
                "price", 1299.99,
                "stock", 50,
                "category", "Electrónica"
        );

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop HP"));
    }

    @Test
    @DisplayName("POST /products — 400 con datos inválidos (nombre vacío)")
    void createProduct_invalidData_returns400() throws Exception {
        var body = Map.of(
                "name", "",           // inválido: blank
                "price", -5,          // inválido: negativo
                "stock", 10,
                "category", "Tech"
        );

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("DELETE /products/{id} — 204 No Content")
    void deleteProduct_returns204() throws Exception {
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }
}
