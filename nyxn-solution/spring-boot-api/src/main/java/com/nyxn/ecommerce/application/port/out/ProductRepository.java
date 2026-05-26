package com.nyxn.ecommerce.application.port.out;

import com.nyxn.ecommerce.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * PUERTO DE SALIDA: Abstracción que el dominio/aplicación usa para persistir datos.
 * La implementación concreta (JPA, MongoDB, etc.) vive en infraestructura.
 * El dominio NO conoce nada de JPA ni SQL.
 */
public interface ProductRepository {

    Page<Product> findAll(Pageable pageable);

    Optional<Product> findById(Long id);

    Product save(Product product);

    void deleteById(Long id);

    boolean existsById(Long id);
}
