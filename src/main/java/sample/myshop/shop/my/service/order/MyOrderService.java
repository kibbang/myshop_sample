package sample.myshop.shop.my.service.order;

import sample.myshop.order.domain.Order;
import sample.myshop.shop.my.domain.dto.MyOrderDetailDto;
import sample.myshop.shop.my.domain.dto.MyOrderListDto;

import java.util.List;

public interface MyOrderService {
    List<MyOrderListDto> getMyOrderList(Long memberId);
    MyOrderDetailDto getMyOrderDetail(String orderNo, Long memberId);
    void cancelMyOrder(String orderNo, Long memberId);
}
