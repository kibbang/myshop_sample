package sample.myshop.shop.my.cart.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.auth.SessionUser;
import sample.myshop.common.exception.BadRequestException;
import sample.myshop.common.exception.NotFoundException;
import sample.myshop.order.domain.dto.VariantSnapshotDto;
import sample.myshop.order.repository.OrderRepository;
import sample.myshop.shop.my.cart.domain.CartItem;
import sample.myshop.shop.my.cart.domain.dto.*;
import sample.myshop.shop.my.cart.repository.CartRepository;

import java.util.List;

import static sample.myshop.auth.SessionConst.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public void addItem(HttpSession session, SessionUser loginUser, CartAddRequestDto request) {

        String sessionId = getOrCreateCartSessionId(session);
        Long memberId = (loginUser != null) ? loginUser.getMemberId() : null;

        VariantSnapshotDto variant = orderRepository.findVariantForOrder(request.getProductId(), request.getVariantId());
        if (variant == null) {
            throw new NotFoundException("유효하지 않은 상품 옵션입니다.");
        }

        CartItem existing = findExisting(memberId, sessionId, request.getVariantId());
        if (existing != null) {
            existing.increaseQuantity(request.getQuantity());
            return;
        }

        CartItem item = CartItem.create(sessionId, memberId, request.getProductId(), request.getVariantId(), request.getQuantity());
        cartRepository.save(item);
    }

    @Override
    public List<CartItem> getCartItems(HttpSession session, SessionUser loginUser) {
        String sessionId = getOrCreateCartSessionId(session);
        Long memberId = (loginUser != null) ? loginUser.getMemberId() : null;

        if (memberId != null) {
            return cartRepository.findAllByMemberId(memberId);
        }

        return cartRepository.findAllBySessionId(sessionId);
    }

    @Override
    @Transactional
    public void changeQuantity(HttpSession session, SessionUser loginUser, Long cartItemId, CartChangeQuantityRequestDto req) {
        CartItem target = findOwnedCartItem(session, loginUser, cartItemId);
        target.changeQuantity(req.getQuantity());
    }

    @Override
    @Transactional
    public void removeItem(HttpSession session, SessionUser loginUser, Long cartItemId) {
        CartItem target = findOwnedCartItem(session, loginUser, cartItemId);
        cartRepository.delete(target);
    }

    @Override
    public CartListDto getCartView(HttpSession session, SessionUser loginUser) {
        String sessionId = getOrCreateCartSessionId(session);
        Long memberId = (loginUser != null) ? loginUser.getMemberId() : null;

        List<CartListItemDto> items;
        if (memberId != null) {
            items = cartRepository.findCartListItemsByMemberId(memberId);
        } else {
            items = cartRepository.findCartListItemsBySessionId(sessionId);
        }

        return new CartListDto(items);
    }

    @Override
    public List<CartItemOrderSourceDto> getSelectedCartItemsForOrder(Long memberId, List<Long> selectedCartItemIds) {
        // 1차 서비스 방어
        if (selectedCartItemIds == null || selectedCartItemIds.isEmpty()) {
            throw new BadRequestException("선택된 카트 주문 아이템이 없습니다.");
        }

        List<CartItemOrderSourceDto> selectedForOrder = cartRepository.findSelectedForOrder(memberId, selectedCartItemIds);

        // 조회 후 검증
        if (selectedForOrder.size() != selectedCartItemIds.size()) {
            throw new BadRequestException("유효하지 않은 카트 항목이 포함되어 있습니다.");
        }
        return selectedForOrder;
    }

    @Override
    public List<CartItemOrderSourceDto> getAllCartItemsForOrder(Long memberId) {
        return cartRepository.findAllForOrder(memberId);
    }

    @Override
    @Transactional
    public void removeItems(Long memberId, List<Long> cartItemIds) {
        if (memberId == null) {
            throw new BadRequestException("회원 정보가 없습니다.");
        }

        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return;
        }

        cartRepository.deleteByMemberIdAndIds(memberId, cartItemIds);
    }

    private CartItem findExisting(Long memberId, String sessionId, Long variantId) {
        if (memberId != null) {
            return cartRepository.findByMemberIdAndVariantId(memberId, variantId).orElse(null);
        }
        return cartRepository.findBySessionIdAndVariantId(sessionId, variantId).orElse(null);
    }

    private CartItem findOwnedCartItem(HttpSession session, SessionUser loginUser, Long cartItemId) {
        String sessionId = getOrCreateCartSessionId(session);
        Long memberId = (loginUser != null) ? loginUser.getMemberId() : null;

        if (memberId != null) {
            return cartRepository.findByIdAndMemberId(cartItemId, memberId)
                    .orElseThrow(() -> new NotFoundException("장바구니 항목을 찾을 수 없습니다."));
        }

        return cartRepository.findByIdAndSessionId(cartItemId, sessionId)
                .orElseThrow(() -> new NotFoundException("장바구니 항목을 찾을 수 없습니다."));
    }

    private String getOrCreateCartSessionId(HttpSession session) {
        Object value = session.getAttribute(CART_SESSION_ID);
        if (value instanceof String string && !string.isBlank()) {
            return string;
        }

        String generated = session.getId();
        session.setAttribute(CART_SESSION_ID, generated);

        return generated;
    }
}
