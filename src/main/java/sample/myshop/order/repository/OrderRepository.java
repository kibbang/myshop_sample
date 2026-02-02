package sample.myshop.order.repository;

import sample.myshop.admin.product.domain.Inventory;
import sample.myshop.order.domain.Order;
import sample.myshop.order.domain.dto.DefaultVariantSnapshotDto;

public interface OrderRepository {
    DefaultVariantSnapshotDto findDefaultVariantForOrder(Long productId);

    void save(Order order);
    Order findByIdWithOrderItems(Long orderId);
}
