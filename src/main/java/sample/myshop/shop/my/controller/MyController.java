package sample.myshop.shop.my.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sample.myshop.auth.LoginUser;
import sample.myshop.auth.SessionUser;
import sample.myshop.shop.my.domain.dto.MyOrderDetailDto;
import sample.myshop.shop.my.domain.dto.MyOrderListDto;
import sample.myshop.shop.my.service.order.MyOrderService;

import java.util.List;

import static sample.myshop.auth.SessionConst.*;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/my")
public class MyController {
    private final MyOrderService myOrderService;

    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(LOGIN_USER);

        List<MyOrderListDto> myOrderList = myOrderService.getMyOrderList(sessionUser.getMemberId());

        model.addAttribute("orderList", myOrderList);
        addContentView(model, "shop/my/order/list :: content");

        return "shop/layout/base";
    }

    @GetMapping("/orders/{orderNo}")
    public String orderDetails(@PathVariable String orderNo, @LoginUser SessionUser sessionUser, Model model) {
        MyOrderDetailDto myOrderDetail = myOrderService.getMyOrderDetail(orderNo, sessionUser.getMemberId());

        model.addAttribute("order", myOrderDetail);
        addContentView(model, "shop/my/order/detail :: content");

        return "shop/layout/base";
    }

    @PostMapping("/orders/{orderNo}/cancel")
    public String orderCancel(@PathVariable String orderNo, @LoginUser SessionUser sessionUser, Model model, RedirectAttributes redirectAttributes) {
        try {
            myOrderService.cancelMyOrder(orderNo, sessionUser.getMemberId());

            redirectAttributes.addFlashAttribute("message", "주문이 취소되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "주문 취소에 실패했습니다.");
        }

        return "redirect:/my/orders/{orderNo}";
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
