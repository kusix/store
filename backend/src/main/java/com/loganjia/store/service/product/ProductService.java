package com.loganjia.store.service.product;

import com.loganjia.store.entities.product.Category;
import com.loganjia.store.entities.product.Product;
import com.loganjia.store.entities.user.Address;
import com.loganjia.store.entities.user.Profile;
import com.loganjia.store.entities.user.User;
import com.loganjia.store.exception.EntityNotFoundException;
import com.loganjia.store.mapper.ProductDTO;
import com.loganjia.store.mapper.ProductMapper;
import com.loganjia.store.repositories.product.CategoryRepository;
import com.loganjia.store.repositories.product.ProductRepository;
import com.loganjia.store.repositories.product.specifications.ProductSpec;
import com.loganjia.store.repositories.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@NoArgsConstructor
public class ProductService {

    private UserRepository userRepository;

    private ProductRepository productRepository;

    private CategoryRepository categoryRepository;

    private ProductMapper productMapper;

    @Autowired
    public ProductService(UserRepository userRepository, ProductRepository productRepository, CategoryRepository categoryRepository,ProductMapper productMapper) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public void query() {
        productRepository.findProductByNameLikeOrPriceBetween("t", new BigDecimal("1"), new BigDecimal("10")).forEach(
                product1 -> System.out.println(product1.getName())
        );
    }


    public void fetchUser() {
        User user = userRepository.findByEmail("loganjia@loganjia.com").orElseThrow();
        System.out.println(user);
    }

    @Transactional
    public void fetchAllUsers() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> {
            System.out.println(user);
            user.getAddresses().forEach(System.out::println);
        });
    }

    public List<Product> fetchProductAll(int pageNumber, int PageSize, String name, BigDecimal minPrice, BigDecimal maxPrice) {
        Specification<Product> spec = Specification.where(null);
        if (name != null) {
            spec = spec.and(ProductSpec.hasName(name));
        }
        if (minPrice != null) {
            spec = spec.and(ProductSpec.hasPriceGreaterThanOrEqual(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(ProductSpec.hasPriceLessThanOrEqual(maxPrice));
        }
        Pageable pageable = PageRequest.of(pageNumber, PageSize, Sort.by("price").ascending());
        Page<Product> productsPage = productRepository.findAll(spec, pageable);
        System.out.println("fetchProductAll##################"+spec.toString());
        productsPage.getContent().forEach(p -> System.out.println(p.getName()));
        return productsPage.getContent();
    }

    @Transactional
    public void update() {
        productRepository.updateProductPriceByCategoryId((byte) 2, BigDecimal.valueOf(10d));
    }

    @Transactional
    public void delete() {
//        var user = userRepository.findById(9L).orElseThrow();
//        var address = user.getAddresses().get(0);
//        user.removeAddress(address);
//        userRepository.save(user);
        userRepository.deleteById(9L);
    }

    @Transactional
    public void addNewProductToWishlist() {
        categoryRepository.findById((byte) 2).ifPresent(category -> {
            var product = Product.builder()
                    .name("new product1")
                    .description("description")
                    .price(new BigDecimal("2.5"))
                    .category(category)
                    .build();
            productRepository.save(product);
            userRepository.findById(10L).ifPresent(user -> {
                user.addWishlist(product);
                userRepository.save(user);
            });
        });
    }

    @Transactional
    public void deleteProduct() {
        productRepository.deleteById(3L);

    }

    @Transactional
    public void persist() {
        var user = User.builder()
                .password("123456")
                .name("loganjia")
                .email("loganjia@loganjia.com")
                .build();

        var address = Address.builder()
                .street("street")
                .city("city")
                .state("state")
                .zip("zip")
                .build();

        var profile = Profile.builder()
                .bio("bio")
                .phoneNumber("123456789")
                .build();

        var category = Category.builder()
                .name("pets")
                .build();

        var product = Product.builder()
                .name("product")
                .description("description")
                .price(new BigDecimal("1.5"))
                .build();
        category.getProducts().add(product);
        product.setCategory(category);

//        user.addProfile(profile);
        user.addTag("white label");
        user.addAddress(address);
        user.addWishlist(product);
        System.out.println(user);
        userRepository.save(user);
    }

    public Product getProductById(long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("cannot find product by id [%s]".formatted(id))
        );
    }

    public ProductDTO upsertProduct(ProductDTO productDTO) {
        Product product;
        if (productDTO.id() == 0) {
            // Create new product
            product = new Product();
            product.setName(productDTO.name());
            product.setPrice(productDTO.price());
            setCategory(productDTO, product);
        } else {
            Optional<Product> existingProduct = productRepository.findById(productDTO.id());
            if (existingProduct.isPresent()) {
                product = existingProduct.get();
                product.setName(productDTO.name());
                product.setPrice(productDTO.price());
                if (productDTO.category().id() != 0 && !product.getCategory().getId().equals(productDTO.category().id())) {
                    setCategory(productDTO, product);
                    //update category
                }
            } else {
                throw new EntityNotFoundException("Product with id [%s] not found".formatted(productDTO.id()));
            }
        }
        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    private void setCategory(ProductDTO productDTO, Product product) {
        Optional<Category> category = categoryRepository.findById(productDTO.category().id());
        if (category.isPresent()) {
            product.setCategory(category.get());
        } else {
            throw new EntityNotFoundException("Category with id [%s] not found".formatted(productDTO.category().id()));
        }
    }
}
