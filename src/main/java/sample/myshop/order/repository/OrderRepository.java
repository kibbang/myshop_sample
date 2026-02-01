package sample.myshop.order.repository;

import sample.myshop.admin.product.domain.Inventory;
import sample.myshop.order.domain.Order;
import sample.myshop.order.domain.dto.DefaultVariantSnapshotDto;

public interface OrderRepository {
    DefaultVariantSnapshotDto findDefaultVariant(Long productId);
    Inventory findInventoryForUpdateByVariantId(Long variantId);

    void save(Order order);
}
