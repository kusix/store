package com.loganjia.store.mapper;

import com.loganjia.store.entities.product.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toDTO(Product product);

    List<ProductDTO> toDTOList(List<Product> products);

    Product toEntityList(ProductDTO productDTO);

}

