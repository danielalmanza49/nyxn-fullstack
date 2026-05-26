package com.nyxn.ecommerce.application.port.in;

import com.nyxn.ecommerce.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * PUERTO DE ENTRADA: Define el contrato que la capa de aplicación expone al mundo exterior.
 * Los adaptadores primarios (REST Controller) dependen de esta interfaz, NO de la implementación.
 */
public interface ProductUseCase {

    Page<Product> getAllProducts(Pageable pageable);

    Product getProductById(Long id);

    Product createProduct(CreateProductCommand command);

    Product updateProduct(Long id, UpdateProductCommand command);

    void deleteProduct(Long id);

    // === Comandos tipados (evitan parámetros primitivos sueltos) ===

    record CreateProductCommand(
            String name,
            String description,
            java.math.BigDecimal price,
            Integer stock,
            String category
    ) {}

    record UpdateProductCommand(
            String name,
            String description,
            java.math.BigDecimal price,
            Integer stock,
            String category
    ) {}
}
