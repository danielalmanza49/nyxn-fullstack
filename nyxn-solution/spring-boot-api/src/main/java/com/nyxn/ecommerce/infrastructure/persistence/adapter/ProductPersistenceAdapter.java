package com.nyxn.ecommerce.infrastructure.persistence.adapter;

import com.nyxn.ecommerce.application.port.out.ProductRepository;
import com.nyxn.ecommerce.domain.model.Product;
import com.nyxn.ecommerce.infrastructure.persistence.entity.ProductEntity;
import com.nyxn.ecommerce.infrastructure.persistence.repository.JpaProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * ADAPTADOR SECUNDARIO: Implementa el puerto de salida ProductRepository.
 * Traduce entre el modelo de dominio (Product) y la entidad JPA (ProductEntity).
 */
@Component
public class ProductPersistenceAdapter implements ProductRepository {

    private final JpaProductRepository jpaRepository;

    public ProductPersistenceAdapter(JpaProductRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(this::toDomain);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = toEntity(product);
        ProductEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    // === Mappers Domain <-> Entity (Anti-corruption layer) ===

    private Product toDomain(ProductEntity entity) {
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStock(),
                entity.getCategory(),
                entity.getCreatedAt()
        );
    }

    private ProductEntity toEntity(Product domain) {
        ProductEntity entity = new ProductEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setPrice(domain.getPrice());
        entity.setStock(domain.getStock());
        entity.setCategory(domain.getCategory());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}
