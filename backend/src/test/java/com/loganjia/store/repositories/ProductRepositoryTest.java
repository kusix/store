package com.loganjia.store.repositories;

import com.loganjia.store.dto.ProductDTO;
import com.loganjia.store.entities.product.Product;
import com.loganjia.store.repositories.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest extends AbstractJpaTest {

    @Autowired
    private ProductRepository productRepository;


    @Test
    void findProductByNameContainingIgnoreCase_shouldReturnMatchingProducts() {
        // Given - 测试数据已在 setUp 中准备

        // When
        List<Product> products = productRepository.findProductByNameContainingIgnoreCase("iphone");

        // Then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("iPhone 13");
    }

    @Test
    void updateProductPriceByCategoryId_shouldUpdatePrices() {
        // Given
        BigDecimal newPrice = new BigDecimal("899.99");

        // When
        int updatedCount = productRepository.updateProductPriceByCategoryId((byte) 1, newPrice);

        // Then
        assertThat(updatedCount).isEqualTo(3); // 3个电子产品

        // 验证价格是否更新
        List<Product> electronics = productRepository.findProductByNameContainingIgnoreCase("");
        electronics.stream()
                .filter(p -> p.getCategory().getId() == 1)
                .forEach(p -> assertThat(p.getPrice()).isEqualByComparingTo(newPrice));
    }

    @Test
    void findProductByNameLikeOrPriceBetween_shouldReturnCorrectDTOs() {
        // Given
        String name = "iPad";
        BigDecimal priceAfter = new BigDecimal("30.00");
        BigDecimal priceBefore = new BigDecimal("50.00");

        // When
        List<ProductDTO> results = productRepository.findProductByNameLikeOrPriceBetween(name, priceAfter, priceBefore);

        // Then
        assertThat(results).hasSize(3); // Effective Java (name), Spring in Action (price), iPad Air (price)

        // 验证DTO内容
        assertThat(results)
                .extracting(ProductDTO::getName)
                .contains("Effective Java", "Spring in Action", "iPad Air");
    }

}