package sample.myshop.shop.my.cart.service;

import jakarta.servlet.http.HttpSession;
import sample.myshop.auth.SessionUser;
import sample.myshop.shop.my.cart.domain.CartItem;
import sample.myshop.shop.my.cart.domain.dto.CartAddRequestDto;
import sample.myshop.shop.my.cart.domain.dto.CartChangeQuantityRequestDto;
import sample.myshop.shop.my.cart.domain.dto.CartItemOrderSourceDto;
import sample.myshop.shop.my.cart.domain.dto.CartListDto;

import java.util.List;

public interface CartService {
    void addItem(HttpSession session, SessionUser loginUser, CartAddRequestDto request);

    List<CartItem> getCartItems(HttpSession session, SessionUser loginUser);

    void changeQuantity(HttpSession session, SessionUser loginUser, Long cartItemId, CartChangeQuantityRequestDto req);

    void removeItem(HttpSession session, SessionUser loginUser, Long cartItemId);

    CartListDto getCartView(HttpSession session, SessionUser loginUser);

    List<CartItemOrderSourceDto> getSelectedCartItemsForOrder(Long memberId, List<Long> selectedCartItemIds);

    List<CartItemOrderSourceDto> getAllCartItemsForOrder(Long memberId);

    // 카트 다건삭제
    void removeItems(Long memberId, List<Long> cartItemIds);
}
