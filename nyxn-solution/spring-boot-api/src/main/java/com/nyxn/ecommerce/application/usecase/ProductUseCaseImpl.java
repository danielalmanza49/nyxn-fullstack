package com.nyxn.ecommerce.application.usecase;

import com.nyxn.ecommerce.application.port.in.ProductUseCase;
import com.nyxn.ecommerce.application.port.out.ProductRepository;
import com.nyxn.ecommerce.domain.exception.ProductNotFoundException;
import com.nyxn.ecommerce.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CASO DE USO: Orquesta la lógica de negocio usando puertos de entrada/salida.
 * No contiene lógica de presentación ni detalles de infraestructura.
 */
@Service
@Transactional
public class ProductUseCaseImpl implements ProductUseCase {

    private final ProductRepository productRepository;

    // Inyección de dependencias por constructor (Clean Architecture + testabilidad)
    public ProductUseCaseImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public Product createProduct(CreateProductCommand command) {
        Product product = new Product(
                command.name(),
                command.description(),
                command.price(),
                command.stock(),
                command.category()
        );
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, UpdateProductCommand command) {
        Product existing = getProductById(id);
        existing.setName(command.name());
        existing.setDescription(command.description());
        existing.setPrice(command.price());
        existing.setStock(command.stock());
        existing.setCategory(command.category());
        return productRepository.save(existing);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }
}
