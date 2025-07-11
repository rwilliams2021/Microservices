package com.microservices.order_service;

import com.microservices.order_service.stubs.InventoryClientStub;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.testcontainers.containers.MySQLContainer;
import wiremock.org.hamcrest.Matchers;

import static wiremock.org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class OrderServiceApplicationTests {

    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.3.0");
    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    void shouldSubmitOrder() {
        String submitOrderRequest = "{\"skuCode\": \"iphone_15\", \"price\": 10, \"quantity\": 1}";
        InventoryClientStub.stubInventoryCall("iphone_15", 1, true);

        var responseBodyString = RestAssured.given()
                                            .contentType("application/json")
                                            .body(submitOrderRequest)
                                            .when()
                                            .post("/api/order")
                                            .then()
                                            .log().all()
                                            .statusCode(201)
                                            .extract()
                                            .body().asString();

        assertThat(responseBodyString, Matchers.is("Order placed successfully"));
    }

}
