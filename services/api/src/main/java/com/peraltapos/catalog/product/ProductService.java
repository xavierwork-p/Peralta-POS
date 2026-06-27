package com.peraltapos.catalog.product;

import com.peraltapos.common.web.BusinessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> list(String search) {
        Sort sort = Sort.by("name").ascending();
        List<Product> products = (search == null || search.isBlank())
                ? productRepository.findAll(sort)
                : productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(search, search, sort);

        return products.stream().map(ProductResponse::from).toList();
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        productRepository.findBySkuIgnoreCase(request.sku()).ifPresent(existing -> {
            throw new BusinessException("Ya existe un producto con ese codigo interno");
        });

        Product product = new Product();
        product.updateFrom(request);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(UUID id, ProductRequest request) {
        Product product = findEntity(id);
        productRepository.findBySkuIgnoreCase(request.sku())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Ya existe un producto con ese codigo interno");
                });

        product.updateCatalogFrom(request);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public ProductResponse get(UUID id) {
        return ProductResponse.from(findEntity(id));
    }

    @Transactional(readOnly = true)
    public BigDecimal inventoryCostValue() {
        // Para el dashboard inicial calculamos en memoria. Cuando crezca el volumen,
        // esto se cambia por una consulta SQL agregada.
        return productRepository.findAll()
                .stream()
                .map(product -> product.getCostPrice().multiply(product.getCurrentStock()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public long lowStockCount() {
        return productRepository.findAll()
                .stream()
                .filter(Product::isLowStock)
                .count();
    }

    private Product findEntity(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));
    }
}
