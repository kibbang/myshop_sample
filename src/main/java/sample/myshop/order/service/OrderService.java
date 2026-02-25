package sample.myshop.order.service;

import sample.myshop.admin.order.domain.dto.web.OrderDetailDto;
import sample.myshop.admin.order.domain.dto.web.OrderListItemDto;
import sample.myshop.admin.order.domain.dto.web.OrderSearchConditionDto;
import sample.myshop.order.domain.Order;
import sample.myshop.order.session.OrderDeliveryRequestDto;
import sample.myshop.order.session.OrderPrepareSession;
import sample.myshop.shop.order.domain.dto.web.OrderPrepareInfoDto;
import sample.myshop.shop.order.domain.dto.web.OrderPreparePageDto;

import java.util.List;

public interface OrderService {
    String placeOrder(Long productId, Long variantId, int quantity, String buyerLoginId, OrderDeliveryRequestDto orderDeliveryRequestDto);

    void cancelOrder(Long orderId);

    OrderDetailDto getOrder(Long orderId);

    List<OrderListItemDto> searchOrders(OrderSearchConditionDto condition, int page, int size);

    Long getTotalOrderCount(OrderSearchConditionDto condition);

    Order getOrderWithItemsByOrderNo(String orderNo);

    OrderPrepareInfoDto getOrderPrepareInfo(Long productId, Long variantId, int quantity, String loginId);

    OrderPreparePageDto getOrderPreparePageInfo(OrderPrepareSession prepareSession, String loginId);

    String placeOrderFromPrepare(OrderPrepareSession prepareSession, String buyerLoginId, OrderDeliveryRequestDto deliveryForm);
}
