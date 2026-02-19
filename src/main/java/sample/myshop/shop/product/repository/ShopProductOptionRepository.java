package sample.myshop.shop.product.repository;

import sample.myshop.admin.product.domain.Option;

import java.util.List;

public interface ShopProductOptionRepository {
    List<Option> findByProductId(Long productId);
}
