package com.microservices.product_service.service;

import com.microservices.product_service.dto.ProductRequest;
import com.microservices.product_service.dto.ProductResponse;
import com.microservices.product_service.mapper.ProductMapper;
import com.microservices.product_service.model.Product;
import com.microservices.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private ProductRequest productRequest;
    private Product product;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        productRequest = ProductRequest.builder()
                                       .name("iPhone 13")
                                       .description("iPhone 13 description")
                                       .price(BigDecimal.valueOf(1200))
                                       .build();

        product = Product.builder()
                         .id("1")
                         .name("iPhone 13")
                         .description("iPhone 13 description")
                         .price(BigDecimal.valueOf(1200))
                         .build();

        productResponse = ProductResponse.builder()
                                         .id("1")
                                         .name("iPhone 13")
                                         .description("iPhone 13 description")
                                         .price(BigDecimal.valueOf(1200))
                                         .build();
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProduct() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // When
        productService.createProduct(productRequest);

        // Then
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(productCaptor.capture());

        Product capturedProduct = productCaptor.getValue();
        assertThat(capturedProduct.getName()).isEqualTo(productRequest.getName());
        assertThat(capturedProduct.getDescription()).isEqualTo(productRequest.getDescription());
        assertThat(capturedProduct.getPrice()).isEqualTo(productRequest.getPrice());
        assertThat(capturedProduct.getId()).isNull(); // ID should be null before save
    }

    @Test
    @DisplayName("Should get all products successfully")
    void shouldGetAllProducts() {
        // Given
        Product product2 = Product.builder()
                                  .id("2")
                                  .name("Samsung Galaxy S24")
                                  .description("Latest Samsung flagship")
                                  .price(BigDecimal.valueOf(1000))
                                  .build();

        ProductResponse productResponse2 = ProductResponse.builder()
                                                          .id("2")
                                                          .name("Samsung Galaxy S24")
                                                          .description("Latest Samsung flagship")
                                                          .price(BigDecimal.valueOf(1000))
                                                          .build();

        List<Product> products = Arrays.asList(product, product2);
        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.productToProductResponse(eq(product))).thenReturn(productResponse);
        when(productMapper.productToProductResponse(eq(product2))).thenReturn(productResponse2);

        // When
        List<ProductResponse> result = productService.getAllProducts();

        // Then
        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(2)).productToProductResponse(any(Product.class));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("iPhone 13");
        assertThat(result.get(0).getPrice()).isEqualTo(BigDecimal.valueOf(1200));
        assertThat(result.get(1).getName()).isEqualTo("Samsung Galaxy S24");
        assertThat(result.get(1).getPrice()).isEqualTo(BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void shouldReturnEmptyListWhenNoProducts() {
        // Given
        when(productRepository.findAll()).thenReturn(List.of());

        // When
        List<ProductResponse> result = productService.getAllProducts();

        // Then
        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(0)).productToProductResponse(any(Product.class));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle single product correctly")
    void shouldHandleSingleProduct() {
        // Given
        List<Product> products = Collections.singletonList(product);
        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.productToProductResponse(eq(product))).thenReturn(productResponse);

        // When
        List<ProductResponse> result = productService.getAllProducts();

        // Then
        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).productToProductResponse(eq(product));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo("1");
        assertThat(result.getFirst().getName()).isEqualTo("iPhone 13");
        assertThat(result.getFirst().getDescription()).isEqualTo("iPhone 13 description");
        assertThat(result.getFirst().getPrice()).isEqualTo(BigDecimal.valueOf(1200));
    }
}