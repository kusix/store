package com.loganjia.store.repositories.product;

import com.loganjia.store.dto.ProductDTO;
import com.loganjia.store.entities.product.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    public List<Product> findProductByNameContainingIgnoreCase(String name);


    @Modifying
    @Query("update product p set p.price = :newPrice where p.category.id = :categoryId")
    int updateProductPriceByCategoryId(@Param("categoryId") Byte categoryId, @Param("newPrice") BigDecimal newPrice);

    //@Query("select p from product p where p.name like %:name% or (p.price > :priceAfter and p.price < :priceBefore)")

    @Query("select p.name as name,p.price as price from product p where p.name like %:name% or p.price between :priceAfter and :priceBefore")
    List<ProductDTO> findProductByNameLikeOrPriceBetween(@Param("name") String name, @Param("priceAfter") BigDecimal priceAfter, @Param("priceBefore") BigDecimal priceBefore);


}
