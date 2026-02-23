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
import sample.myshop.admin.product.repository.VariantRepository;
import sample.myshop.common.exception.BadRequestException;
import sample.myshop.common.exception.NotFoundException;
import sample.myshop.member.domain.Member;
import sample.myshop.member.repository.MemberRepository;
import sample.myshop.order.domain.Order;
import sample.myshop.order.domain.OrderItem;
import sample.myshop.order.domain.dto.VariantSnapshotDto;
import sample.myshop.order.repository.OrderRepository;
import sample.myshop.order.session.OrderDeliveryRequestDto;
import sample.myshop.release.domain.OrderRelease;
import sample.myshop.shop.order.domain.dto.web.OrderPrepareInfoDto;
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
    private final VariantRepository variantRepository;

    @Override
    @Transactional
    public String placeOrder(Long productId, Long variantId, int quantity, String buyerLoginId, OrderDeliveryRequestDto orderDeliveryRequestDto) {

        if (quantity < 1) {
            throw new BadRequestException("수량은 1개 이상이어야 합니다.");
        }

        // 주문자 ID 찾기
        Member member = memberRepository.findByLoginId(buyerLoginId);
        if (member == null) {
            throw new NotFoundException("주문자 정보를 찾을 수 없습니다.");
        }

        // variant 찾기
        VariantSnapshotDto variantSnapshot = orderRepository.findVariantForOrder(productId, variantId);

        if (variantSnapshot == null) {
            // productId + variantId 조합이 맞지 않거나 variant 없음
            throw new BadRequestException("해당 상품의 옵션 정보를 찾을 수 없습니다.");
        }

        // 인벤토리 찾기
        Inventory inventoryForUpdate = inventoryRepository.findInventoryForUpdateByVariantId(variantSnapshot.getVariantId());

        // 재고 감소 (재고가 부족할 경우 주문을 생성할 필요가 없음 그렇기 때문에 주문 생성 -> 감소 패턴 보다 나음)
        inventoryForUpdate.decreaseQuantity(quantity);

        // unitPrice 세팅
        int unitPrice = variantSnapshot.getVariantPrice() != null
                ? variantSnapshot.getVariantPrice()
                : variantSnapshot.getBasePrice();

        // OrderItem 생성 (OrderItem 조각을 생성하고 그것들을 Order에 담는다)
        OrderItem orderItem = OrderItem.createOrderItem(
                productId,
                variantSnapshot.getVariantId(),
                variantSnapshot.getSku(),
                variantSnapshot.getProductName(),
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

        recoveryItemStock(targetOrder);
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

    @Override
    public OrderPrepareInfoDto getOrderPrepareInfo(Long productId, Long variantId, int quantity, String loginId) {
        if (quantity < 1) {
            throw new BadRequestException("수량은 1개 이상이어야 합니다.");
        }

        // 선택한 variant가 해당 product 소속인지 포함해서 조회
        VariantSnapshotDto variantSnapshot = orderRepository.findVariantForOrder(productId, variantId);

        // 재고 조회 (prepare 화면은 락 없이 조회)
        Inventory inventory = inventoryRepository.findByVariantId(variantSnapshot.getVariantId());

        if (inventory == null) {
            throw new NotFoundException("재고 정보를 찾을 수 없습니다.");
        }

        int unitPrice = variantSnapshot.getVariantPrice() != null
                ? variantSnapshot.getVariantPrice()
                : variantSnapshot.getBasePrice();

        int totalPrice = unitPrice * quantity;

        return new OrderPrepareInfoDto(
                productId,
                variantSnapshot.getVariantId(),
                variantSnapshot.getProductName(),
                variantSnapshot.getSku(),
                "-",
                quantity,
                inventory.getStockQuantity(),
                unitPrice,
                totalPrice
        );
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

    private void recoveryItemStock(Order targetOrder) {
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
}
