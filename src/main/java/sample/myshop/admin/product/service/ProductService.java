package sample.myshop.admin.product.service;

import sample.myshop.admin.product.domain.dto.web.ProductCreateDto;

public interface ProductService {
    Long createProduct(ProductCreateDto productCreateDto);
}
