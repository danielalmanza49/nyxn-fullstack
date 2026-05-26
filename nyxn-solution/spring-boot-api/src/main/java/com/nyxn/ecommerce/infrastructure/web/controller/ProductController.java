package com.nyxn.ecommerce.infrastructure.web.controller;

import com.nyxn.ecommerce.application.port.in.ProductUseCase;
import com.nyxn.ecommerce.domain.model.Product;
import com.nyxn.ecommerce.infrastructure.web.dto.ProductDto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ADAPTADOR PRIMARIO (REST): Recibe peticiones HTTP y delega al caso de uso.
 * No contiene lógica de negocio. Solo traduce HTTP <-> Dominio.
 */
@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Gestión del catálogo de productos del e-commerce")
public class ProductController {

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Retorna una página de productos con soporte de paginación y ordenamiento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    })
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<ProductResponse> response = productUseCase.getAllProducts(pageable)
                .map(this::toResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        return ResponseEntity.ok(toResponse(productUseCase.getProductById(id)));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo producto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductUseCase.CreateProductCommand command = new ProductUseCase.CreateProductCommand(
                request.name(), request.description(), request.price(),
                request.stock(), request.category()
        );
        Product created = productUseCase.createProduct(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        ProductUseCase.UpdateProductCommand command = new ProductUseCase.UpdateProductCommand(
                request.name(), request.description(), request.price(),
                request.stock(), request.category()
        );
        return ResponseEntity.ok(toResponse(productUseCase.updateProduct(id, command)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto del catálogo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // === Mapper local: Dominio -> DTO de respuesta ===
    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.getCreatedAt()
        );
    }
}
