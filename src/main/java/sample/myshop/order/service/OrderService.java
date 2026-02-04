package sample.myshop.order.service;

import sample.myshop.admin.order.domain.dto.web.OrderDetailDto;
import sample.myshop.admin.order.domain.dto.web.OrderListItemDto;
import sample.myshop.admin.order.domain.dto.web.OrderSearchConditionDto;
import sample.myshop.order.domain.Order;
import sample.myshop.order.session.OrderDeliveryRequestDto;

import java.util.List;

public interface OrderService {
    String placeOrder(Long productId, int quantity, String buyerLoginId, OrderDeliveryRequestDto orderDeliveryRequestDto);

    void cancelOrder(Long orderId);

    OrderDetailDto getOrder(Long orderId);

    List<OrderListItemDto> searchOrders(OrderSearchConditionDto condition, int page, int size);

    Long getTotalOrderCount(OrderSearchConditionDto condition);

    Order getOrderWithItemsByOrderNo(String orderNo);
}
