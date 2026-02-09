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
import sample.myshop.order.service.OrderService;
import sample.myshop.order.session.OrderDeliveryRequestDto;
import sample.myshop.order.session.OrderPrepareSession;
import sample.myshop.shop.order.domain.dto.web.OrderCreateForm;
import sample.myshop.shop.product.domain.dto.web.ShopProductDetailDto;
import sample.myshop.shop.product.service.ShopProductService;

@Controller
@Slf4j
@RequestMapping("/orders")
@RequiredArgsConstructor
public class ShopOrderController {

    private final OrderService orderService;
    private final ShopProductService shopProductService;

    @PostMapping("/prepare")
    public String prepare(@ModelAttribute OrderCreateForm form, HttpSession session, Model model) {

        // 1) 주문 준비 세션 저장 (URL에 노출 X)
        session.setAttribute(SessionConst.ORDER_PREPARE, new OrderPrepareSession(form.getProductId(), form.getQuantity()));

        // 2) 비로그인이면 로그인으로
        SessionUser user = (SessionUser) session.getAttribute(SessionConst.LOGIN_USER);

        if (user == null) {
            return "redirect:/login?redirect=%2Forders%2Fprepare";
        }

        // 3) 로그인 상태면 주문서로
        return "redirect:/orders/prepare";
    }

    @GetMapping("/prepare")
    public String prepareView(HttpSession session, Model model) {

        SessionUser user = (SessionUser) session.getAttribute(SessionConst.LOGIN_USER);
        if (user == null) {
            return "redirect:/login?redirect=%2Forders%2Fprepare";
        }

        OrderPrepareSession orderPrepareSession = (OrderPrepareSession) session.getAttribute(SessionConst.ORDER_PREPARE);

        log.info("prepare view. sessionId={}, hasPrepare={}",
                session.getId(), orderPrepareSession != null);

        if (orderPrepareSession == null) {
            return "redirect:/";
        }

        ShopProductDetailDto productDetail = shopProductService.getDetail(orderPrepareSession.getProductId());
        model.addAttribute("product", productDetail);

        model.addAttribute("quantity", orderPrepareSession.getQuantity());
        model.addAttribute("orderDeliveryForm", new OrderDeliveryRequestDto());

        // content fragment 주입 방식이면:
        addContentView(model, "shop/order/prepare :: content");

        return "shop/layout/base";
    }

    @PostMapping
    public String placeOrder(
            @Validated @ModelAttribute OrderDeliveryRequestDto  deliveryForm,
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

        if (orderPrepareSession == null) {
            return "redirect:/";
        }

        session.removeAttribute(SessionConst.ORDER_PREPARE);

        String placedOrderNo = orderService.placeOrder(
                orderPrepareSession.getProductId(),
                orderPrepareSession.getQuantity(),
                sessionUser.getLoginId(),
                deliveryForm
        );

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
     * 뷰에 컨텐츠 삽입
     * @param model
     * @param attributeTarget
     */
    private static void addContentView(Model model, String attributeTarget) {
        model.addAttribute("content", attributeTarget);
    }
}
