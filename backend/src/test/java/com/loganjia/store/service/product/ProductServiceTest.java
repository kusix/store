package com.loganjia.store.service.product;

import com.loganjia.store.entities.product.Category;
import com.loganjia.store.entities.product.Product;
import com.loganjia.store.exception.EntityNotFoundException;
import com.loganjia.store.mapper.CategoryDTO;
import com.loganjia.store.mapper.ProductDTO;
import com.loganjia.store.mapper.ProductMapper;
import com.loganjia.store.repositories.product.CategoryRepository;
import com.loganjia.store.repositories.product.ProductRepository;
import com.loganjia.store.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;


    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private ProductDTO newProductDTO;
    private ProductDTO existingProductDTO;
    private Product existingProduct;
    private Category category;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        category = new Category((byte)1, "Electronics",null);

        newProductDTO = new ProductDTO(
                0L,
                "New Product",
                new BigDecimal("99.99"),
                new CategoryDTO((byte)1, "Electronics")
        );

        existingProductDTO = new ProductDTO(
                1L,
                "Updated Product",
                new BigDecimal("199.99"),
                new CategoryDTO((byte)1, "Electronics")
        );

        existingProduct = new Product(
                1L,
                "Original Product",
                new BigDecimal("99.99"),
                "description",
                category
        );
    }
    @Test
    void upsertProduct_shouldCreateNewProduct_whenIdIsZero() {
        // 准备Mock行为
        when(categoryRepository.findById((byte)1)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productMapper.toDTO(any(Product.class))).thenReturn(newProductDTO);

        // 执行测试
        ProductDTO result = productService.upsertProduct(newProductDTO);

        // 验证
        assertThat(result).isEqualTo(newProductDTO);
        verify(productRepository).save(any(Product.class));
        verify(categoryRepository).findById((byte)1);
    }

    @Test
    void upsertProduct_shouldUpdateExistingProduct_whenIdExists() {
        // 准备Mock行为
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productMapper.toDTO(any(Product.class))).thenReturn(existingProductDTO);

        // 执行测试
        ProductDTO result = productService.upsertProduct(existingProductDTO);

        // 验证
        assertThat(result).isEqualTo(existingProductDTO);
        assertThat(existingProduct.getName()).isEqualTo("Updated Product");
        assertThat(existingProduct.getPrice()).isEqualTo(new BigDecimal("199.99"));
        verify(productRepository).findById(1L);
        verify(productRepository).save(existingProduct);
    }

    @Test
    void upsertProduct_shouldUpdateCategory_whenCategoryChanged() {
        // 准备测试数据
        Category newCategory = new Category((byte)2, "Books",null);
        ProductDTO dtoWithNewCategory = new ProductDTO(
                1L,
                "Updated Product",
                new BigDecimal("199.99"),
                new CategoryDTO((byte)2, "Books")
        );

        // 准备Mock行为
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById((byte)2)).thenReturn(Optional.of(newCategory));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productMapper.toDTO(any(Product.class))).thenReturn(dtoWithNewCategory);

        // 执行测试
        ProductDTO result = productService.upsertProduct(dtoWithNewCategory);

        // 验证
        assertThat(result).isEqualTo(dtoWithNewCategory);
        assertThat(existingProduct.getCategory()).isEqualTo(newCategory);
        verify(categoryRepository).findById((byte)2);
    }

    @Test
    void upsertProduct_shouldThrowException_whenProductNotFound() {
        // 准备Mock行为
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // 准备测试数据
        ProductDTO nonExistingProductDTO = new ProductDTO(
                999L,
                "Non-existing Product",
                new BigDecimal("99.99"),
                new CategoryDTO((byte)2, "Electronics")
        );

        // 执行和验证
        assertThatThrownBy(() -> productService.upsertProduct(nonExistingProductDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Product with id [999] not found");
    }

    @Test
    void upsertProduct_shouldThrowException_whenCategoryNotFound() {
        // 准备Mock行为
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById((byte)99)).thenReturn(Optional.empty());

        // 准备测试数据
        ProductDTO dtoWithInvalidCategory = new ProductDTO(
                1L,
                "Product",
                new BigDecimal("99.99"),
                new CategoryDTO((byte)99, "Invalid Category")
        );

        // 执行和验证
        assertThatThrownBy(() -> productService.upsertProduct(dtoWithInvalidCategory))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Category with id [99] not found");
    }
}