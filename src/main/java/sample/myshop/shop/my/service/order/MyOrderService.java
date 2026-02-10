package sample.myshop.shop.my.service.order;

import sample.myshop.shop.my.domain.dto.MyOrderDetailDto;
import sample.myshop.shop.my.domain.dto.MyOrderListDto;

import java.util.List;

public interface MyOrderService {
    List<MyOrderListDto> getMyOrderList(Long memberId);
    MyOrderDetailDto getMyOrderDetail(String orderNo, Long memberId);
}
