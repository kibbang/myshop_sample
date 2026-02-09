package sample.myshop.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.admin.order.domain.dto.web.OrderDetailDto;
import sample.myshop.admin.order.domain.dto.web.OrderItemDto;
import sample.myshop.admin.order.domain.dto.web.OrderListItemDto;
import sample.myshop.admin.order.domain.dto.web.OrderSearchConditionDto;
import sample.myshop.admin.product.domain.Inventory;
import sample.myshop.admin.product.repository.InventoryRepository;
import sample.myshop.member.domain.Member;
import sample.myshop.member.repository.MemberRepository;
import sample.myshop.order.domain.Order;
import sample.myshop.order.domain.OrderItem;
import sample.myshop.order.domain.dto.DefaultVariantSnapshotDto;
import sample.myshop.order.repository.OrderRepository;
import sample.myshop.order.session.OrderDeliveryRequestDto;
import sample.myshop.release.domain.OrderRelease;
import sample.myshop.utils.OrderGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public String placeOrder(Long productId, int quantity, String buyerLoginId, OrderDeliveryRequestDto orderDeliveryRequestDto) {
        // 주문자 ID 찾기
        Member member = memberRepository.findByLoginId(buyerLoginId);

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

        Order order = Order.createOrder(
                newOrderId,
                buyerLoginId,
                member.getId(),
                orderDeliveryRequestDto.getReceiverName(),
                orderDeliveryRequestDto.getReceiverPhone(),
                orderDeliveryRequestDto.getReceiverZipcode(),
                orderDeliveryRequestDto.getReceiverBaseAddress(),
                orderDeliveryRequestDto.getReceiverDetailAddress(),
                orderDeliveryRequestDto.getDeliveryMemo()
        );
        order.addOrderItem(orderItem);

        // 배송 생성
        OrderRelease orderRelease = OrderRelease.create();
        order.createRelease(orderRelease);

        orderRepository.save(order);

        return order.getOrderNo();
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        // 타겟 주문 조회
        Order targetOrder = orderRepository.findByIdWithDetail(orderId);

        // 주문 상태 변경 (주문 및 출고상태 체크)
        targetOrder.cancel();

        // 각 아이템별 재고 복구 (락 걸고 조회)
        targetOrder.getOrderItems().stream()
                // 아이템이 2개이상 일경우 락의 순서가 엇갈리면 데드락이 날 가능성이 있음
                // 방지 차원에서 순서 소팅
                .sorted(Comparator.comparingLong(OrderItem::getVariantId))
                .forEach(orderItem -> {
                    Inventory orderedInventory = inventoryRepository.findInventoryForUpdateByVariantId(orderItem.getVariantId());
                    orderedInventory.increaseQuantity(orderItem.getQuantity());
                });
    }

    @Override
    public List<OrderListItemDto> searchOrders(OrderSearchConditionDto condition, int page, int size) {
        return orderRepository.findOrders(condition, page, size);
    }

    @Override
    public Long getTotalOrderCount(OrderSearchConditionDto condition) {
        return orderRepository.countOrders(condition);
    }

    @Override
    public OrderDetailDto getOrder(Long orderId) {
        Order orderWithItems = orderRepository.findByIdWithDetail(orderId);

        List<OrderItemDto> orderItems = convertOrderItemsToDto(orderWithItems);

        return OrderDetailDto.of(
                orderWithItems.getId(),
                orderWithItems.getOrderNo(),
                orderWithItems.getBuyerLoginId(),
                orderWithItems.getStatus(),
                orderWithItems.getTotalAmount(),
                orderWithItems.getCreatedAt(),
                orderItems,
                orderWithItems.getReceiverName() == null ? "" : orderWithItems.getReceiverName(),
                orderWithItems.getReceiverPhone() == null ? "" : orderWithItems.getReceiverPhone(),
                orderWithItems.getReceiverZip() == null ? "" : orderWithItems.getReceiverZip(),
                orderWithItems.getReceiverBaseAddress() == null ? "" : orderWithItems.getReceiverBaseAddress(),
                orderWithItems.getReceiverDetailAddress() == null ? "" : orderWithItems.getReceiverDetailAddress(),
                orderWithItems.getDeliveryMemo() == null ? "" : orderWithItems.getDeliveryMemo(),
                orderWithItems.getRelease() == null ? null : orderWithItems.getRelease().getStatus()
        );
    }

    @Override
    public Order getOrderWithItemsByOrderNo(String orderNo) {
        return orderRepository.findByOrderNoWithOrderItems(orderNo);
    }

    /**
     * OrderItem DTO 조립
     * @param orderWithItems
     * @return
     */
    private static List<OrderItemDto> convertOrderItemsToDto(Order orderWithItems) {
        List<OrderItemDto> orderItems = new ArrayList<>();

        for (OrderItem orderItem : orderWithItems.getOrderItems()) {
            OrderItemDto orderItemDto = OrderItemDto.of(
                    orderItem.getProductId(),
                    orderItem.getVariantId(),
                    orderItem.getSkuSnapshot(),
                    orderItem.getProductNameSnapshot(),
                    orderItem.getUnitPrice(),
                    orderItem.getQuantity(),
                    orderItem.getLineAmount()
            );

            orderItems.add(orderItemDto);
        }
        return orderItems;
    }
}
