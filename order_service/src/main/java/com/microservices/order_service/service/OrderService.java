package com.microservices.order_service.service;

import com.microservices.order_service.dto.OrderRequest;
import com.microservices.order_service.model.Order;
import com.microservices.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setPrice(orderRequest.price());
        order.setSkuCode(orderRequest.skuCode());
        order.setQuantity(orderRequest.quantity());

        orderRepository.save(order);
        //Call inventory service and place order if product is in stock
//        InventoryResponse[] inventoryResponseArray =
//                webClientBuilder.build().get().uri("http://inventory-service/api/inventory",
//                                                   uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
//                                .retrieve()
//                                .bodyToMono(InventoryResponse[].class).block();
//
//        boolean allProductsInStock =
//                Arrays.stream(Objects.requireNonNull(inventoryResponseArray)).allMatch(InventoryResponse::isInStock);
//        if (allProductsInStock) {
//            orderRepository.save(order);
//        }
//        else {
//            throw new IllegalArgumentException("Product is not in stock");
//        }
    }
}
