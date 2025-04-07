package com.loganjia.store.repositories.product;

import com.loganjia.store.entities.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Byte> {
}
