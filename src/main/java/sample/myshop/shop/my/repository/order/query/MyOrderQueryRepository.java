package sample.myshop.shop.my.repository.order.query;

import sample.myshop.shop.my.domain.dto.MyOrderDetailDto;
import sample.myshop.shop.my.domain.dto.MyOrderListDto;

import java.util.List;

public interface MyOrderQueryRepository {
    List<MyOrderListDto> getMyOrderList(Long memberId);
    MyOrderDetailDto getMyOrderDetail(String orderNo, Long memberId);
}
