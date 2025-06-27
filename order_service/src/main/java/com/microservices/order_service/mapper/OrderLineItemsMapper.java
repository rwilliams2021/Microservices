package com.microservices.order_service.mapper;

import com.microservices.order_service.dto.OrderLineItemsDto;
import com.microservices.order_service.model.OrderLineItems;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderLineItemsMapper {
    OrderLineItems mapToOrderLineItems(OrderLineItemsDto orderLineItemsDto);
}
