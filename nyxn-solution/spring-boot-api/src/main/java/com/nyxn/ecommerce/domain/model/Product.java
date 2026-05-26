package com.nyxn.ecommerce.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad de dominio puro — sin dependencias de Spring ni JPA.
 * El núcleo de la Arquitectura Hexagonal.
 */
public class Product {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String category;
    private LocalDateTime createdAt;

    // Constructor para creación (sin ID — lo asigna la infraestructura)
    public Product(String name, String description, BigDecimal price,
                   Integer stock, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.createdAt = LocalDateTime.now();
    }

    // Constructor completo (para reconstrucción desde persistencia)
    public Product(Long id, String name, String description, BigDecimal price,
                   Integer stock, String category, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.createdAt = createdAt;
    }

    // === Lógica de negocio dentro del dominio ===

    public boolean hasStock() {
        return this.stock != null && this.stock > 0;
    }

    public boolean isStockCritical() {
        return this.stock != null && this.stock < 10;
    }

    public void decrementStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalStateException("Stock insuficiente para el producto: " + this.name);
        }
        this.stock -= quantity;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
