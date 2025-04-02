package com.loganjia.store.controller;

import com.loganjia.store.mapper.ProductDTO;
import com.loganjia.store.mapper.ProductMapper;
import com.loganjia.store.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    private final ProductMapper productMapper;

    @GetMapping("fetch")
    public List<ProductDTO> fetch(@RequestParam int page, @RequestParam int size,@RequestParam(required = false) String name) {
        return productMapper.toDTOList(productService.fetchProductAll(page, size, name, BigDecimal.valueOf(0), null));
    }

    @GetMapping("get")
    public ProductDTO get(@RequestParam long id) {
        return productMapper.toDTO(productService.getProductById(id));
    }

    @PostMapping("upsert")
    public ProductDTO upsert(@RequestBody ProductDTO productDTO) {
        return productService.upsertProduct(productDTO);
    }

}


