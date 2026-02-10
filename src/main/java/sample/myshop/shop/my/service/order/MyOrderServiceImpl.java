package sample.myshop.shop.my.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.admin.product.domain.Inventory;
import sample.myshop.admin.product.repository.InventoryRepository;
import sample.myshop.common.exception.OrderNotFoundException;
import sample.myshop.order.domain.Order;
import sample.myshop.order.domain.OrderItem;
import sample.myshop.shop.my.domain.dto.MyOrderDetailDto;
import sample.myshop.shop.my.domain.dto.MyOrderListDto;
import sample.myshop.shop.my.repository.order.query.MyOrderQueryRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyOrderServiceImpl implements MyOrderService {

    private final MyOrderQueryRepository myOrderQueryRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public List<MyOrderListDto> getMyOrderList(Long memberId) {
        return myOrderQueryRepository.getMyOrderList(memberId);
    }

    @Override
    public MyOrderDetailDto getMyOrderDetail(String orderNo, Long memberId) {
        MyOrderDetailDto order = myOrderQueryRepository.getMyOrderDetail(orderNo, memberId);

        if (order == null) {
            throw new OrderNotFoundException("주문을 찾을 수 없습니다.");
        }

        return order;
    }

    @Override
    @Transactional
    public void cancelMyOrder(String orderNo, Long memberId) {
        Order targetOrder = myOrderQueryRepository.cancel(orderNo, memberId);

        targetOrder.cancel();

        recoveryItemStock(targetOrder);
    }

    /**
     * 재고 복구
     * @param targetOrder
     */
    private void recoveryItemStock(Order targetOrder) {
        targetOrder.getOrderItems().stream()
                // 아이템이 2개이상 일경우 락의 순서가 엇갈리면 데드락이 날 가능성이 있음
                // 방지 차원에서 순서 소팅
                .sorted(Comparator.comparingLong(OrderItem::getVariantId))
                .forEach(orderItem -> {
                    Inventory orderedInventory = inventoryRepository.findInventoryForUpdateByVariantId(orderItem.getVariantId());
                    orderedInventory.increaseQuantity(orderItem.getQuantity());
                });
    }
}
