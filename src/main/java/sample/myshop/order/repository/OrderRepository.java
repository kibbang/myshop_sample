package sample.myshop.order.repository;

import sample.myshop.admin.order.domain.dto.web.OrderListItemDto;
import sample.myshop.admin.order.domain.dto.web.OrderSearchConditionDto;
import sample.myshop.admin.product.domain.Inventory;
import sample.myshop.order.domain.Order;
import sample.myshop.order.domain.dto.DefaultVariantSnapshotDto;

import java.util.List;

public interface OrderRepository {
    DefaultVariantSnapshotDto findDefaultVariantForOrder(Long productId);

    void save(Order order);
    Order findByIdWithOrderItems(Long orderId);

    List<OrderListItemDto> findOrders(OrderSearchConditionDto condition, int page, int size);

    Long countOrders(OrderSearchConditionDto condition);
}
