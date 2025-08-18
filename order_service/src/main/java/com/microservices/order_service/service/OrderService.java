package com.microservices.order_service.service;

import com.microservices.order_service.client.InventoryClient;
import com.microservices.order_service.dto.OrderRequest;
import com.microservices.order_service.event.OrderPlacedEvent;
import com.microservices.order_service.model.Order;
import com.microservices.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest) {

        boolean isInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if (isInStock) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price());
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());
            orderRepository.save(order);

            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(order.getOrderNumber(),
                                                                     "email");
            log.info("Start - sending OrderPlacedEvent {} to kafka topic order-placed-event", orderPlacedEvent);
            kafkaTemplate.send("order-placed-event", orderPlacedEvent);
            log.info("End - sending OrderPlacedEvent {} to kafka topic order-placed-event", orderPlacedEvent);
        }
        else {
            throw new IllegalArgumentException(
                    "Product with skuCode " + orderRequest.skuCode() + " is not in stock" + "for quantity " +
                    orderRequest.quantity());
        }
    }
}
