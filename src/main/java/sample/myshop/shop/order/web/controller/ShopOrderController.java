package sample.myshop.shop.order.web.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sample.myshop.auth.SessionConst;
import sample.myshop.auth.SessionUser;
import sample.myshop.order.domain.Order;
import sample.myshop.order.facade.OrderCheckoutFacade;
import sample.myshop.order.service.OrderService;
import sample.myshop.order.session.OrderDeliveryRequestDto;
import sample.myshop.order.session.OrderPrepareLineSession;
import sample.myshop.order.session.OrderPrepareSession;
import sample.myshop.shop.my.cart.domain.dto.CartItemOrderSourceDto;
import sample.myshop.shop.my.cart.service.CartService;
import sample.myshop.shop.order.domain.dto.web.OrderCreateForm;
import sample.myshop.shop.order.domain.dto.web.OrderPreparePageDto;

import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/orders")
@RequiredArgsConstructor
public class ShopOrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final OrderCheckoutFacade orderCheckoutFacade;

    @GetMapping("/prepare")
    public String prepareView(HttpSession session, Model model) {

        SessionUser user = (SessionUser) session.getAttribute(SessionConst.LOGIN_USER);
        if (user == null) {
            return "redirect:/login?redirect=%2Forders%2Fprepare";
        }

        OrderPrepareSession orderPrepareSession = (OrderPrepareSession) session.getAttribute(SessionConst.ORDER_PREPARE);

        log.info("prepare view. sessionId={}, hasPrepare={}",
                session.getId(), orderPrepareSession != null);

        if (orderPrepareSession == null
                || orderPrepareSession.getLines() == null
                || orderPrepareSession.getLines().isEmpty()) {
            return "redirect:/";
        }

        OrderPreparePageDto preparePage = orderService.getOrderPreparePageInfo(orderPrepareSession, user.getLoginId());

        model.addAttribute("preparePage", preparePage);
        model.addAttribute("orderDeliveryForm", new OrderDeliveryRequestDto());

        // content fragment 주입 방식이면:
        addContentView(model, "shop/order/prepare :: content");

        return "shop/layout/base";
    }

    @PostMapping("/prepare")
    public String prepare(@ModelAttribute OrderCreateForm form, HttpSession session, Model model) {

        // 1) 주문 준비 세션 저장 (URL에 노출 X)
        OrderPrepareSession prepareSession = OrderPrepareSession.directSingle(
                form.getProductId(),
                form.getVariantId(),
                form.getQuantity()
        );

        session.setAttribute(SessionConst.ORDER_PREPARE, prepareSession);

        // 2) 비로그인이면 로그인으로
        SessionUser user = (SessionUser) session.getAttribute(SessionConst.LOGIN_USER);

        if (user == null) {
            return "redirect:/login?redirect=%2Forders%2Fprepare";
        }

        // 3) 로그인 상태면 주문서로
        return "redirect:/orders/prepare";
    }

    /**
     * 카트 내에서 선택하여 주문 하는 경우
     * @param selectedCartItemIds
     * @param session
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/prepare/cart")
    public String prepareFromCart(
            @RequestParam(name = "selectedCartItemIds", required = false) List<Long> selectedCartItemIds,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        SessionUser user = (SessionUser) session.getAttribute(SessionConst.LOGIN_USER);
        if (user == null) {
            return "redirect:/login?redirect=%2Fmy%2Fcart";
        }

        if (selectedCartItemIds == null || selectedCartItemIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "주문할 상품을 선택해주세요.");
            return "redirect:/my/cart";
        }

        // memberId 기준으로 본인 장바구니 항목만 조회되도록 서비스에서 보장
        List<CartItemOrderSourceDto> cartItems = cartService.getSelectedCartItemsForOrder(
                user.getMemberId(),
                selectedCartItemIds
        );

        if (cartItems == null || cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "주문 가능한 장바구니 상품이 없습니다.");
            return "redirect:/my/cart";
        }

        return makeCartOrderPrepare(session, redirectAttributes, cartItems);
    }

    /**
     * 카트 내에서 전부 주문 하는 경우
     * @param session
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/prepare/cart-all")
    public String prepareAllFromCart(
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        SessionUser user = (SessionUser) session.getAttribute(SessionConst.LOGIN_USER);
        if (user == null) {
            return "redirect:/login?redirect=%2Fmy%2Fcart";
        }

        List<CartItemOrderSourceDto> cartItems = cartService.getAllCartItemsForOrder(user.getMemberId());

        if (cartItems == null || cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "장바구니가 비어 있습니다.");
            return "redirect:/my/cart";
        }

        return makeCartOrderPrepare(session, redirectAttributes, cartItems);
    }

    @PostMapping
    public String placeOrder(
            @Validated @ModelAttribute OrderDeliveryRequestDto deliveryForm,
            HttpSession session,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/orders/prepare";
        }

        SessionUser sessionUser = (SessionUser) session.getAttribute(SessionConst.LOGIN_USER);

        if (sessionUser == null) {
            return "redirect:/login?redirect=%2Forders%2Fprepare";
        }

        OrderPrepareSession orderPrepareSession = (OrderPrepareSession) session.getAttribute(SessionConst.ORDER_PREPARE);

        if (orderPrepareSession == null
                || orderPrepareSession.getLines() == null
                || orderPrepareSession.getLines().isEmpty()) {
            return "redirect:/";
        }

        String placedOrderNo = orderCheckoutFacade.checkoutFromPrepare(
                orderPrepareSession,
                sessionUser.getMemberId(),
                sessionUser.getLoginId(),
                deliveryForm
        );

        // 프리페어 세션은 주문 생성하고 지우는게 맞지..
        session.removeAttribute(SessionConst.ORDER_PREPARE);

        redirectAttributes.addFlashAttribute("message", "주문 완료");

        return "redirect:/orders/" + placedOrderNo;
    }

    @GetMapping("/{orderNo}")
    public String orderResult(@PathVariable String orderNo, Model model) {

        Order order = orderService.getOrderWithItemsByOrderNo(orderNo);

        model.addAttribute("order", order);
        addContentView(model, "shop/order/result :: content");

        return "shop/layout/base";
    }

    /**
     * 카트 주문 프리페어 생성
     * @param session
     * @param redirectAttributes
     * @param cartItems
     * @return
     */
    private String makeCartOrderPrepare(HttpSession session, RedirectAttributes redirectAttributes, List<CartItemOrderSourceDto> cartItems) {
        List<OrderPrepareLineSession> lines = new ArrayList<>();
        List<Long> cartItemIds = new ArrayList<>();

        for (CartItemOrderSourceDto item : cartItems) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                redirectAttributes.addFlashAttribute("error", "장바구니 수량이 올바르지 않은 상품이 있습니다.");
                return "redirect:/my/cart";
            }

            lines.add(OrderPrepareLineSession.of(
                    item.getProductId(),
                    item.getVariantId(),
                    item.getQuantity()
            ));
            cartItemIds.add(item.getCartItemId());
        }

        OrderPrepareSession prepareSession = OrderPrepareSession.fromCart(lines, cartItemIds);
        session.setAttribute(SessionConst.ORDER_PREPARE, prepareSession);

        return "redirect:/orders/prepare";
    }

    /**
     * 뷰에 컨텐츠 삽입
     * @param model
     * @param attributeTarget
     */
    private static void addContentView(Model model, String attributeTarget) {
        model.addAttribute("content", attributeTarget);
    }
}
