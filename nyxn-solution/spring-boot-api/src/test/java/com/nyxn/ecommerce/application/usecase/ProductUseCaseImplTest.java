package com.nyxn.ecommerce.application.usecase;

import com.nyxn.ecommerce.application.port.in.ProductUseCase;
import com.nyxn.ecommerce.application.port.out.ProductRepository;
import com.nyxn.ecommerce.domain.exception.ProductNotFoundException;
import com.nyxn.ecommerce.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductUseCase — Unit Tests")
class ProductUseCaseImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductUseCaseImpl productUseCase;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product(1L, "Laptop HP", "Laptop empresarial",
                new BigDecimal("1299.99"), 50, "Electrónica", java.time.LocalDateTime.now());
    }

    @Test
    @DisplayName("getProductById — debe retornar el producto cuando existe")
    void getProductById_whenExists_returnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Product result = productUseCase.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Laptop HP");
        assertThat(result.getPrice()).isEqualByComparingTo("1299.99");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getProductById — debe lanzar ProductNotFoundException cuando no existe")
    void getProductById_whenNotExists_throwsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productUseCase.getProductById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("createProduct — debe persistir y retornar el producto creado")
    void createProduct_validCommand_savesAndReturns() {
        var command = new ProductUseCase.CreateProductCommand(
                "Monitor Samsung", "Monitor 4K", new BigDecimal("499.99"), 30, "Electrónica");

        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        Product result = productUseCase.createProduct(command);

        assertThat(result).isNotNull();
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("deleteProduct — debe lanzar excepción si el producto no existe")
    void deleteProduct_whenNotExists_throwsException() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productUseCase.deleteProduct(99L))
                .isInstanceOf(ProductNotFoundException.class);

        verify(productRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("getAllProducts — debe retornar página con resultados")
    void getAllProducts_returnsPage() {
        var pageable = PageRequest.of(0, 20);
        Page<Product> page = new PageImpl<>(List.of(sampleProduct));
        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<Product> result = productUseCase.getAllProducts(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Laptop HP");
    }
}
