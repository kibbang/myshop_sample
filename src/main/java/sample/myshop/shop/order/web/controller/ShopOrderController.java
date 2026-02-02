package sample.myshop.shop.order.web.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
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
import sample.myshop.shop.order.domain.dto.web.OrderCreateForm;

@Controller
@Slf4j
@RequestMapping("/orders")
@RequiredArgsConstructor
public class ShopOrderController {

    private final OrderService orderService;

    @PostMapping
    public String placeOrder(@Validated @ModelAttribute OrderCreateForm orderCreateForm, Model model, HttpSession session, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/products/" + orderCreateForm.getProductId();
        }

        SessionUser sessionUser = (SessionUser) session.getAttribute(SessionConst.LOGIN_USER);

        if (sessionUser == null) {
            return "redirect:/login?redirect=/products/" + orderCreateForm.getProductId();
        }

        String placedOrderNo = orderService.placeOrder(
                orderCreateForm.getProductId(),
                orderCreateForm.getQuantity(),
                sessionUser.getLoginId()
        );

        redirectAttributes.addFlashAttribute("message", "주문 완료");

        return "redirect:/orders/" + placedOrderNo;
    }

    @GetMapping("/{orderNo}")
    public String orderResult(@PathVariable String orderNo, Model model) {

        Order order = orderService.getOrderWithItemsByOrderNo(orderNo);

        model.addAttribute("order", order);
        model.addAttribute("content", "shop/order/result :: content");

        return "shop/layout/base";
    }
}
