package sample.myshop.shop.product.repository;

import sample.myshop.admin.product.domain.Variant;

public interface ShopVariantRepository {
    Variant findById(Long variantId);
    Long findDefaultVariantIdByProductId(Long productId);
}
