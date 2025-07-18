package com.microservices.apigateway.routes;

import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;

@Configuration
class Routes {

    private String productServiceUrl = "http://localhost:8080";
    private String orderServiceUrl = "http://localhost:8081";
    private String inventoryServiceUrl = "http://localhost:8082";

    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        return route("product_service")
                       .route(RequestPredicates.path("/api/product"),
                              HandlerFunctions.http(productServiceUrl))
                       .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return route("order_service")
                       .route(RequestPredicates.path("/api/order"),
                              HandlerFunctions.http(orderServiceUrl))
                       .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute() {
        return route("inventory_service")
                       .route(RequestPredicates.path("/api/inventory"),
                              HandlerFunctions.http(inventoryServiceUrl))
                       .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute() {
        return route("product_service_swagger")
                       .route(RequestPredicates.path("/aggregate/product-service/v3/api-docs"),
                              HandlerFunctions.http(productServiceUrl))
                       .filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceSwaggerCircuitBreaker",
                                                                            URI.create("forward:/fallbackRoute")))
                       .filter(setPath("/api-docs"))
                       .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute() {
        return route("order_service_swagger")
                       .route(RequestPredicates.path("/aggregate/order-service/v3/api-docs"),
                              HandlerFunctions.http(orderServiceUrl))
                       .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceSwaggerCircuitBreaker",
                                                                            URI.create("forward:/fallbackRoute")))
                       .filter(setPath("/api-docs"))
                       .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoute() {
        return route("inventory_service_swagger")
                       .route(RequestPredicates.path("/aggregate/inventory-service/v3/api-docs"),
                              HandlerFunctions.http(inventoryServiceUrl))
                       .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceSwaggerCircuitBreaker",
                                                                            URI.create("forward:/fallbackRoute")))
                       .filter(setPath("/api-docs"))
                       .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return route("fallbackRoute")
                       .GET("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                                                       .body("Service Unavailable, please try again " +
                                                                             "later"))
                       .build();
    }
}
