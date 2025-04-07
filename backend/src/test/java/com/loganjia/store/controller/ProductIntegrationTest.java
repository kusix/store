package com.loganjia.store.controller;

import com.loganjia.store.entities.product.Product;
import com.loganjia.store.mapper.CategoryDTO;
import com.loganjia.store.mapper.ProductDTO;
import com.loganjia.store.repositories.AbstractJpaTest;
import com.loganjia.store.service.product.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductIntegrationTest extends AbstractJpaTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void fetch_shouldReturnPaginatedProducts() {
        webTestClient.get()
                .uri("/api/product/fetch?page=0&size=2")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDTO.class)
                .hasSize(2)
                .value(products -> {
                    assertThat(products)
                            .extracting(ProductDTO::name)
                            .containsAnyOf("iPhone 15", "MacBook Pro", "iPad Air", "Spring in Action", "Effective Java");
                });
    }

    @Test
    void fetch_withNameFilter_shouldReturnFilteredProducts(@Autowired WebTestClient webTestClient) {
        webTestClient.get()
                .uri("/api/product/fetch?page=0&size=10&name=Mac")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDTO.class)
                .hasSize(1)
                .value(products -> {
                    assertThat(products.get(0).name()).isEqualTo("MacBook Pro");
                });
    }

    @Test
    void get_shouldReturnProductById(@Autowired WebTestClient webTestClient) {
        // 先获取一个存在的产品ID
        Long productId = productService.fetchProductAll(0, 1, null, null, null).get(0).getId();

        webTestClient.get()
                .uri("/api/product/get?id=" + productId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDTO.class)
                .value(product -> {
                    assertThat(product.id()).isEqualTo(productId);
                    assertThat(product.name()).isNotNull();
                });
    }

    @Test
    void get_withInvalidId_shouldReturnNotFound(@Autowired WebTestClient webTestClient) {
        webTestClient.get()
                .uri("/api/product/get?id=9999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Commit
    void upsert_shouldCreateNewProduct(@Autowired WebTestClient webTestClient) {
        CategoryDTO category = new CategoryDTO(
                productService.fetchProductAll(0, 1, null, null, null).get(0)
                        .getCategory()
                        .getId(),
                "Electronics"
        );

        ProductDTO newProduct = new ProductDTO(
                0,
                "New Product",
                new BigDecimal("299.99"),
                category
        );

        webTestClient.post()
                .uri("/api/product/upsert")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newProduct)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDTO.class)
                .value(savedProduct -> {
                    assertThat(savedProduct.id()).isNotNull();
                    assertThat(savedProduct.name()).isEqualTo("New Product");

                });
        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(productService.fetchProductAll(0, 100, null, null, null).size()).isEqualTo(6);

                });
    }

    @Test
    void upsert_shouldUpdateExistingProduct(@Autowired WebTestClient webTestClient) {
        // 获取现有产品
        Product existing = productService.fetchProductAll(0, 1, null, null, null).get(0);
        CategoryDTO category = new CategoryDTO(
                existing.getCategory().getId(),
                existing.getCategory().getName()
        );

        ProductDTO updatedProduct = new ProductDTO(
                existing.getId(),
                "Updated " + existing.getName(),
                existing.getPrice().add(BigDecimal.TEN),
                category
        );

        webTestClient.post()
                .uri("/api/product/upsert")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedProduct)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDTO.class)
                .value(savedProduct -> {
                    assertThat(savedProduct.name()).startsWith("Updated");
                    assertThat(savedProduct.price())
                            .isEqualByComparingTo(existing.getPrice().add(BigDecimal.TEN));
                });

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Product dbProduct = productService.getProductById(existing.getId());
                    assertThat(dbProduct.getName()).startsWith("Updated");
                });
    }

    @Test
    void upsert_withInvalidData_shouldReturnBadRequest(@Autowired WebTestClient webTestClient) {
        ProductDTO invalidProduct = new ProductDTO(
                0,
                "", // 空名称
                BigDecimal.valueOf(-100), // 负价格
                null // 无分类
        );

        webTestClient.post()
                .uri("/api/product/upsert")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidProduct)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
