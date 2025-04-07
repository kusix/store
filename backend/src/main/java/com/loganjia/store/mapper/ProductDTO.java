package com.loganjia.store.mapper;

import java.math.BigDecimal;

public record ProductDTO(long id,String name, BigDecimal price,CategoryDTO category){}
