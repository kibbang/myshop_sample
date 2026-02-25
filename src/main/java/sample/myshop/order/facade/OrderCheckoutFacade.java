package sample.myshop.order.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.common.exception.BadRequestException;
import sample.myshop.order.service.OrderService;
import sample.myshop.order.session.OrderDeliveryRequestDto;
import sample.myshop.order.session.OrderPrepareSession;
import sample.myshop.shop.my.cart.service.CartService;

@Service
@RequiredArgsConstructor
public class OrderCheckoutFacade {
    private final OrderService orderService;
    private final CartService cartService;

    @Transactional
    public String checkoutFromPrepare(
            OrderPrepareSession prepareSession,
            Long memberId,
            String buyerLoginId,
            OrderDeliveryRequestDto deliveryForm
    ) {
        if (prepareSession == null || prepareSession.getLines() == null || prepareSession.getLines().isEmpty()) {
            throw new BadRequestException("주문 프리페어 정보가 없습니다.");
        }

        if (memberId == null) {
            throw new BadRequestException("회원 정보가 없습니다.");
        }

        // 1) 주문 생성 (같은 트랜잭션)
        String orderNo = orderService.placeOrderFromPrepare(prepareSession, buyerLoginId, deliveryForm);

        // 2) 카트 주문이면 카트 삭제 (같은 트랜잭션)
        if (prepareSession.isCart()
                && prepareSession.getCartItemIds() != null
                && !prepareSession.getCartItemIds().isEmpty()) {
            cartService.removeItems(memberId, prepareSession.getCartItemIds());
        }

        return orderNo;
    }
}
