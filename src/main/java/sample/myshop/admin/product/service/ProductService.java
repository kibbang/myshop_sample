package sample.myshop.admin.product.service;

import sample.myshop.admin.product.domain.dto.web.*;

import java.util.List;

public interface ProductService {
    Long createProduct(ProductCreateDto productCreateDto);
    List<ProductListItemDto> searchProducts(ProductSearchConditionDto condition, int page, int size);
    Long getTotalProductCount(ProductSearchConditionDto condition);

    ProductDetailDto showProduct(Long productId);
    void modifyProduct(ProductUpdateDto productUpdateDto);
}
