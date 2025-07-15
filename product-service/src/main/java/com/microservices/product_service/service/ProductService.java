package com.microservices.product_service.service;

import com.microservices.product_service.dto.ProductRequest;
import com.microservices.product_service.dto.ProductResponse;
import com.microservices.product_service.mapper.ProductMapper;
import com.microservices.product_service.model.Product;
import com.microservices.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                                 .name(productRequest.name())
                                 .description(productRequest.description())
                                 .skuCode(productRequest.skuCode())
                                 .price(productRequest.price())
                                 .build();

        productRepository.save(product);
        log.info("Created product with name {} and description {}", productRequest.name(),
                 productRequest.description());
        return productMapper.productToProductResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                       .map(productMapper::productToProductResponse)
                       .collect(Collectors.toList());
    }
}
