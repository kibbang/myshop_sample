package sample.myshop.shop.my.cart.repository;

import sample.myshop.shop.my.cart.domain.CartItem;
import sample.myshop.shop.my.cart.domain.dto.CartItemOrderSourceDto;
import sample.myshop.shop.my.cart.domain.dto.CartListItemDto;

import java.util.List;
import java.util.Optional;

public interface CartRepository {
    void save(CartItem cartItem);

    Optional<CartItem> findById(Long id);

    Optional<CartItem> findByMemberIdAndVariantId(Long memberId, Long variantId);

    Optional<CartItem> findBySessionIdAndVariantId(String sessionId, Long variantId);

    List<CartItem> findAllByMemberId(Long memberId);

    List<CartItem> findAllBySessionId(String sessionId);

    Optional<CartItem> findByIdAndMemberId(Long id, Long memberId);

    Optional<CartItem> findByIdAndSessionId(Long id, String sessionId);

    void delete(CartItem cartItem);

    List<CartListItemDto> findCartListItemsByMemberId(Long memberId);

    List<CartListItemDto> findCartListItemsBySessionId(String sessionId);

    List<CartItemOrderSourceDto> findSelectedForOrder(Long memberId, List<Long> cartItemIds);

    List<CartItemOrderSourceDto> findAllForOrder(Long memberId);

    void deleteByMemberIdAndIds(Long memberId, List<Long> cartItemIds);
}
