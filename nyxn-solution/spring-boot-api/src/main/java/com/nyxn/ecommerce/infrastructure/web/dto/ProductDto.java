package com.nyxn.ecommerce.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDto {

    /**
     * DTO de entrada para crear/actualizar un producto.
     * Aplica Bean Validation estricta en la capa web (no en el dominio).
     */
    @Schema(description = "Payload para crear o actualizar un producto")
    public record ProductRequest(

            @NotBlank(message = "El nombre es obligatorio")
            @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
            @Schema(description = "Nombre del producto", example = "Laptop HP EliteBook")
            String name,

            @Size(max = 2000, message = "La descripción no puede superar 2000 caracteres")
            @Schema(description = "Descripción detallada del producto")
            String description,

            @NotNull(message = "El precio es obligatorio")
            @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
            @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 2 decimales")
            @Schema(description = "Precio unitario", example = "1299.99")
            BigDecimal price,

            @NotNull(message = "El stock es obligatorio")
            @Min(value = 0, message = "El stock no puede ser negativo")
            @Schema(description = "Unidades en inventario", example = "50")
            Integer stock,

            @NotBlank(message = "La categoría es obligatoria")
            @Size(max = 100, message = "La categoría no puede superar 100 caracteres")
            @Schema(description = "Categoría del producto", example = "Electrónica")
            String category

    ) {}

    /**
     * DTO de salida — evita exponer la entidad JPA directamente a la API.
     */
    @Schema(description = "Respuesta de producto")
    public record ProductResponse(
            @Schema(description = "ID único del producto") Long id,
            @Schema(description = "Nombre") String name,
            @Schema(description = "Descripción") String description,
            @Schema(description = "Precio") BigDecimal price,
            @Schema(description = "Stock disponible") Integer stock,
            @Schema(description = "Categoría") String category,
            @Schema(description = "Fecha de creación") LocalDateTime createdAt
    ) {}

    /**
     * Respuesta de error estructurada y consistente.
     */
    @Schema(description = "Respuesta de error de la API")
    public record ErrorResponse(
            int status,
            String error,
            String message,
            LocalDateTime timestamp
    ) {}
}
