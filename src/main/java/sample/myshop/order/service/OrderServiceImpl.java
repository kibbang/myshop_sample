package sample.myshop.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.admin.product.domain.Inventory;
import sample.myshop.admin.product.repository.InventoryRepository;
import sample.myshop.order.domain.Order;
import sample.myshop.order.domain.OrderItem;
import sample.myshop.order.domain.dto.DefaultVariantSnapshotDto;
import sample.myshop.order.repository.OrderRepository;
import sample.myshop.utils.OrderGenerator;

import static sample.myshop.enums.order.OrderStatus.ORDERED;
import static sample.myshop.enums.order.OrderStatus.valueOf;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public String placeOrder(Long productId, int quantity, String buyerLoginId) {
        // 대표 variant 찾기
        DefaultVariantSnapshotDto defaultVariant = orderRepository.findDefaultVariantForOrder(productId);

        // 인벤토리 찾기
        Inventory inventoryForUpdate = inventoryRepository.findInventoryForUpdateByVariantId(defaultVariant.getVariantId());

        // 재고 감소 (재고가 부족할 경우 주문을 생성할 필요가 없음 그렇기 때문에 주문 생성 -> 감소 패턴 보다 나음)
        inventoryForUpdate.decreaseQuantity(quantity);

        // unitPrice 세팅
        int unitPrice = defaultVariant.getVariantPrice() != null
                ? defaultVariant.getVariantPrice()
                : defaultVariant.getBasePrice();

        // OrderItem 생성 (OrderItem 조각을 생성하고 그것들을 Order에 담는다)
        OrderItem orderItem = OrderItem.createOrderItem(
                productId,
                defaultVariant.getVariantId(),
                defaultVariant.getSku(),
                defaultVariant.getProductName(),
                unitPrice,
                quantity
        );

        // Order 생성
        String newOrderId = OrderGenerator.generateOrderNo();

        Order order = Order.createOrder(newOrderId, buyerLoginId);
        order.addOrderItem(orderItem);

        orderRepository.save(order);

        return order.getOrderNo();
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        // 타겟 주문 조회
        Order targetOrder = orderRepository.findByIdWithOrderItems(orderId);

        // 각 아이템별 재고 복구 (락 걸고 조회)
        targetOrder.getOrderItems().forEach(orderItem -> {
            Inventory orderedInventory = inventoryRepository.findInventoryForUpdateByVariantId(orderItem.getVariantId());

            orderedInventory.increaseQuantity(orderItem.getQuantity());
        });

        // 주문 상태 변경
        targetOrder.cancel();
    }

    @Override
    public void getOrder(Long orderId) {

    }
}
