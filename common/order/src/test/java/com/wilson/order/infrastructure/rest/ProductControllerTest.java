package com.wilson.order.infrastructure.rest;

import com.wilson.order.application.ports.inputs.CreateProductUseCase;
import com.wilson.order.application.ports.inputs.GetProductUseCase;
import com.wilson.order.domain.model.Product;
import com.wilson.order.infrastructure.rest.dto.ProductDto;
import com.wilson.order.infrastructure.rest.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductControllerTest {

    @Mock
    private CreateProductUseCase createProductUseCase;

    @Mock
    private GetProductUseCase getProductUseCase;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProductSuccess() {
        // Arrange
        ProductDto request = ProductDto.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stock(100)
                .build();

        Product expectedProduct = Product.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();

        ProductDto expectedDto = ProductDto.builder()
                .id(expectedProduct.getId())
                .name(expectedProduct.getName())
                .description(expectedProduct.getDescription())
                .price(expectedProduct.getPrice())
                .stock(expectedProduct.getStock())
                .build();

        when(createProductUseCase.createProduct(any(), any(), any(), any()))
                .thenReturn(expectedProduct);
        when(productMapper.toDto(expectedProduct))
                .thenReturn(expectedDto);

        // Act
        ResponseEntity<ProductDto> response = productController.createProduct(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(expectedProduct.getId(), response.getBody().getId());
        assertEquals(expectedProduct.getName(), response.getBody().getName());
        assertEquals(expectedProduct.getDescription(), response.getBody().getDescription());
        assertEquals(expectedProduct.getPrice(), response.getBody().getPrice());
        assertEquals(expectedProduct.getStock(), response.getBody().getStock());
    }

    @Test
    void testGetProductSuccess() {
        // Arrange
        UUID productId = UUID.randomUUID();
        Product expectedProduct = Product.builder()
                .id(productId)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stock(100)
                .build();

        ProductDto expectedDto = ProductDto.builder()
                .id(expectedProduct.getId())
                .name(expectedProduct.getName())
                .description(expectedProduct.getDescription())
                .price(expectedProduct.getPrice())
                .stock(expectedProduct.getStock())
                .build();

        when(getProductUseCase.getProduct(productId))
                .thenReturn(Optional.of(expectedProduct));
        when(productMapper.toDto(expectedProduct))
                .thenReturn(expectedDto);

        // Act
        ResponseEntity<ProductDto> response = productController.getProduct(productId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(expectedProduct.getId(), response.getBody().getId());
        assertEquals(expectedProduct.getName(), response.getBody().getName());
        assertEquals(expectedProduct.getDescription(), response.getBody().getDescription());
        assertEquals(expectedProduct.getPrice(), response.getBody().getPrice());
        assertEquals(expectedProduct.getStock(), response.getBody().getStock());
    }

    @Test
    void testGetProductNotFound() {
        // Arrange
        UUID productId = UUID.randomUUID();
        when(getProductUseCase.getProduct(productId))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<ProductDto> response = productController.getProduct(productId);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void testGetAllProductsSuccess() {
        // Arrange
        Product product1 = Product.builder()
                .id(UUID.randomUUID())
                .name("Product 1")
                .description("Description 1")
                .price(new BigDecimal("99.99"))
                .stock(100)
                .build();

        Product product2 = Product.builder()
                .id(UUID.randomUUID())
                .name("Product 2")
                .description("Description 2")
                .price(new BigDecimal("199.99"))
                .stock(50)
                .build();

        ProductDto dto1 = ProductDto.builder()
                .id(product1.getId())
                .name(product1.getName())
                .description(product1.getDescription())
                .price(product1.getPrice())
                .stock(product1.getStock())
                .build();

        ProductDto dto2 = ProductDto.builder()
                .id(product2.getId())
                .name(product2.getName())
                .description(product2.getDescription())
                .price(product2.getPrice())
                .stock(product2.getStock())
                .build();

        List<Product> expectedProducts = Arrays.asList(product1, product2);
        when(getProductUseCase.getAllProducts())
                .thenReturn(expectedProducts);
        when(productMapper.toDto(product1))
                .thenReturn(dto1);
        when(productMapper.toDto(product2))
                .thenReturn(dto2);

        // Act
        ResponseEntity<List<ProductDto>> response = productController.getAllProducts();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        ProductDto resultDto1 = response.getBody().get(0);
        assertEquals(product1.getId(), resultDto1.getId());
        assertEquals(product1.getName(), resultDto1.getName());
        assertEquals(product1.getDescription(), resultDto1.getDescription());
        assertEquals(product1.getPrice(), resultDto1.getPrice());
        assertEquals(product1.getStock(), resultDto1.getStock());

        ProductDto resultDto2 = response.getBody().get(1);
        assertEquals(product2.getId(), resultDto2.getId());
        assertEquals(product2.getName(), resultDto2.getName());
        assertEquals(product2.getDescription(), resultDto2.getDescription());
        assertEquals(product2.getPrice(), resultDto2.getPrice());
        assertEquals(product2.getStock(), resultDto2.getStock());
    }
} 