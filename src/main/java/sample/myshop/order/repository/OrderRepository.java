package sample.myshop.order.repository;

import sample.myshop.admin.order.domain.dto.web.OrderListItemDto;
import sample.myshop.admin.order.domain.dto.web.OrderSearchConditionDto;
import sample.myshop.order.domain.Order;
import sample.myshop.order.domain.dto.VariantSnapshotDto;

import java.util.List;

public interface OrderRepository {
    VariantSnapshotDto findVariantForOrder(Long productId, Long variantId);

    void save(Order order);

    Order findByIdWithDetail(Long orderId);

    List<OrderListItemDto> findOrders(OrderSearchConditionDto condition, int page, int size);

    Long countOrders(OrderSearchConditionDto condition);

    Order findByOrderNoWithOrderItems(String orderNo);
}
