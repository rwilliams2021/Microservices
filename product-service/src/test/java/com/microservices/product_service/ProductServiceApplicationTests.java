package com.microservices.product_service;

import com.microservices.product_service.dto.ProductRequest;
import com.microservices.product_service.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest = getProductRequest();
        String productRequestJson = objectMapper.writeValueAsString(productRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(productRequestJson)).andExpect(status().isCreated());
        Assertions.assertThat(productRepository.findAll().size()).isEqualTo(1);
    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                             .name("iPhone 13")
                             .description("iPhone 13 description")
                             .price(BigDecimal.valueOf(1200))
                             .build();
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        // First, create a product to ensure there's data
        ProductRequest productRequest1 = getProductRequest();
        String productRequestJson1 = objectMapper.writeValueAsString(productRequest1);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(productRequestJson1))
               .andExpect(status().isCreated());

        // Create a second product
        ProductRequest productRequest2 = ProductRequest.builder()
                                                       .name("Samsung Galaxy S24")
                                                       .description("Latest Samsung flagship")
                                                       .price(BigDecimal.valueOf(1000))
                                                       .build();
        String productRequestJson2 = objectMapper.writeValueAsString(productRequest2);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(productRequestJson2))
               .andExpect(status().isCreated());

        // Now test getting all products
        mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
               .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("iPhone 13"))
               .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Samsung Galaxy S24"));
    }

}
