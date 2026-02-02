package sample.myshop.order.service;

import sample.myshop.admin.order.domain.dto.web.OrderListItemDto;
import sample.myshop.admin.order.domain.dto.web.OrderSearchConditionDto;

import java.util.List;

public interface OrderService {
    String placeOrder(Long productId, int quantity, String buyerLoginId);

    void cancelOrder(Long orderId);

    void getOrder(Long orderId);

    List<OrderListItemDto> searchOrders(OrderSearchConditionDto condition, int page, int size);

    Long getTotalOrderCount(OrderSearchConditionDto condition);
}
