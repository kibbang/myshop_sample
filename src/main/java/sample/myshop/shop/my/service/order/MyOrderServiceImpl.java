package sample.myshop.shop.my.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.myshop.shop.my.domain.dto.MyOrderListDto;
import sample.myshop.shop.my.repository.order.query.MyOrderQueryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyOrderServiceImpl implements MyOrderService {

    private final MyOrderQueryRepository myOrderQueryRepository;

    @Override
    public List<MyOrderListDto> getMyOrderList(Long memberId) {
        return myOrderQueryRepository.getMyOrderList(memberId);
    }
}
