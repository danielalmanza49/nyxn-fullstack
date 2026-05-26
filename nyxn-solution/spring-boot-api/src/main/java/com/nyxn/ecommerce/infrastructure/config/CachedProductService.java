package com.nyxn.ecommerce.infrastructure.config;

import com.nyxn.ecommerce.application.port.in.ProductUseCase;
import com.nyxn.ecommerce.domain.model.Product;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

/**
 * Decorador de caché sobre el caso de uso de productos.
 *
 * PATRÓN: Cache-Aside (Lazy Loading)
 * - @Cacheable: Si existe en caché, lo retorna. Si no, ejecuta el método y lo almacena.
 * - @CacheEvict: Invalida la caché cuando el dato cambia (escritura).
 *
 * MITIGACIÓN DE CACHE STAMPEDE:
 * En escenarios de 10,000 req/min, cuando una clave expira simultáneamente:
 * 1. Con Redis + Redisson: usar tryLock() para que solo un thread recargue la caché.
 * 2. TTL con jitter: duration = baseTTL ± random(0, 300 segundos).
 * 3. Background refresh: hilo en background actualiza antes de expiración.
 * 4. Probabilistic Early Expiration (PER): fórmula matemática para expirar antes
 *    de manera probabilística, distribuyendo la carga de recarga.
 */
@Service
public class CachedProductService {

    private final ProductUseCase productUseCase;

    public CachedProductService(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    /**
     * Cache hit → O(1) desde Redis.
     * Cache miss → consulta BD + almacena en Redis con TTL de 1 hora.
     */
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        return productUseCase.getProductById(id);
    }

    /**
     * Al actualizar o eliminar, invalida TODAS las cachés relacionadas
     * para garantizar consistencia eventual.
     */
    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "product-lists", allEntries = true)
    })
    public Product updateProduct(Long id, ProductUseCase.UpdateProductCommand command) {
        return productUseCase.updateProduct(id, command);
    }

    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "product-lists", allEntries = true)
    })
    public void deleteProduct(Long id) {
        productUseCase.deleteProduct(id);
    }

    @CacheEvict(value = "product-lists", allEntries = true)
    public Product createProduct(ProductUseCase.CreateProductCommand command) {
        return productUseCase.createProduct(command);
    }
}
