package com.nyxn.ecommerce.infrastructure.persistence.repository;

import com.nyxn.ecommerce.infrastructure.persistence.entity.ProductEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA — adaptador secundario de persistencia.
 * Implementa los métodos de acceso a datos con soporte de concurrencia.
 */
public interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {

    Page<ProductEntity> findAll(Pageable pageable);

    /**
     * BLOQUEO PESIMISTA: SELECT ... FOR UPDATE
     * Se usa para operaciones críticas de inventario en alta concurrencia (Cyber-Day).
     * Bloquea la fila a nivel de base de datos hasta que la transacción finalice.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductEntity p WHERE p.id = :id")
    Optional<ProductEntity> findByIdWithPessimisticLock(@Param("id") Long id);
}
