package sample.myshop.admin.product.repository;

import sample.myshop.admin.product.domain.Product;
import sample.myshop.admin.product.domain.dto.web.ProductListItemDto;
import sample.myshop.admin.product.domain.dto.web.ProductSearchConditionDto;
import sample.myshop.admin.product.domain.dto.web.SkuStockRowDto;

import java.util.List;

public interface ProductRepository {
    Long save(Product product);

    List<ProductListItemDto> findProducts(ProductSearchConditionDto condition, int page, int size);

    Long countProducts(ProductSearchConditionDto condition);

}
