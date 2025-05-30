package com.wilson.order.application.services;

import com.wilson.order.application.ports.inputs.CreateProductUseCase;
import com.wilson.order.application.ports.outputs.ProductRepositoryPort;
import com.wilson.order.domain.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Service
public class CreateProductService implements CreateProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public CreateProductService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public Product createProduct(String name, String description, BigDecimal price, Integer stock) {
        validateProductParameters(name, description, price, stock);

        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .build();
        
        return productRepositoryPort.save(product);
    }

    private void validateProductParameters(String name, String description, BigDecimal price, Integer stock) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        if (!StringUtils.hasText(description)) {
            throw new IllegalArgumentException("Product description cannot be null or empty");
        }

        if (price == null) {
            throw new IllegalArgumentException("Product price cannot be null");
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }

        if (stock == null) {
            throw new IllegalArgumentException("Product stock cannot be null");
        }

        if (stock < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative");
        }
    }
} 