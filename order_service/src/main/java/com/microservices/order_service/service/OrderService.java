package com.microservices.order_service.service;

import com.microservices.order_service.dto.InventoryResponse;
import com.microservices.order_service.dto.OrderRequest;
import com.microservices.order_service.mapper.OrderLineItemsMapper;
import com.microservices.order_service.model.Order;
import com.microservices.order_service.model.OrderLineItems;
import com.microservices.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderLineItemsMapper orderLineItemsMapper;
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                                                          .stream()
                                                          .map(orderLineItemsMapper::mapToOrderLineItems)
                                                          .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                                     .map(OrderLineItems::getSkuCode)
                                     .toList();
        //Call inventory service and place order if product is in stock
        InventoryResponse[] inventoryResponseArray =
                webClientBuilder.build().get().uri("http://inventory-service/api/inventory",
                                    uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build()).retrieve()
                         .bodyToMono(InventoryResponse[].class).block();

        boolean allProductsInStock =
                Arrays.stream(Objects.requireNonNull(inventoryResponseArray)).allMatch(InventoryResponse::isInStock);
        if (allProductsInStock) {
            orderRepository.save(order);
        }
        else {
            throw new IllegalArgumentException("Product is not in stock");
        }
    }
}
