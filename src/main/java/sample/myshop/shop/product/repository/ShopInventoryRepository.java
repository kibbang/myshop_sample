package sample.myshop.shop.product.repository;

import sample.myshop.admin.product.domain.Inventory;

public interface ShopInventoryRepository {
    Inventory findByVariantId(Long variantId);
}
