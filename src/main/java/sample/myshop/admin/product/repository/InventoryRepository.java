package sample.myshop.admin.product.repository;

import sample.myshop.admin.product.domain.Inventory;

public interface InventoryRepository {
    Inventory findInventoryForUpdateByVariantId(Long variantId);
    void save(Inventory inventory);

    Inventory findForUpdateByProductIdAndVariantId(Long productId, Long variantId);
}
