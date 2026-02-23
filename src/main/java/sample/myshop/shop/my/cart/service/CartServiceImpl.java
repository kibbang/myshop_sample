package sample.myshop.shop.my.cart.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.auth.SessionUser;
import sample.myshop.common.exception.NotFoundException;
import sample.myshop.order.domain.dto.VariantSnapshotDto;
import sample.myshop.order.repository.OrderRepository;
import sample.myshop.shop.my.cart.domain.CartItem;
import sample.myshop.shop.my.cart.domain.dto.CartAddRequestDto;
import sample.myshop.shop.my.cart.domain.dto.CartChangeQuantityRequestDto;
import sample.myshop.shop.my.cart.domain.dto.CartListDto;
import sample.myshop.shop.my.cart.domain.dto.CartListItemDto;
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
