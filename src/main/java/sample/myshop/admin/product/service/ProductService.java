package sample.myshop.admin.product.service;

import sample.myshop.admin.product.domain.dto.web.ProductCreateDto;
import sample.myshop.admin.product.domain.dto.web.ProductListItemDto;
import sample.myshop.admin.product.domain.dto.web.ProductSearchConditionDto;

import java.util.List;

public interface ProductService {
    Long createProduct(ProductCreateDto productCreateDto);
    List<ProductListItemDto> searchProducts(ProductSearchConditionDto condition, int page, int size);
}
