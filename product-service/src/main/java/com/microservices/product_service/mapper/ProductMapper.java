package com.microservices.product_service.mapper;

import com.microservices.product_service.dto.ProductResponse;
import com.microservices.product_service.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse productToProductResponse(Product product);
}