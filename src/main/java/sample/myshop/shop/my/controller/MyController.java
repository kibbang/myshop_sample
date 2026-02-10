package sample.myshop.shop.my.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
        // 타인 주문번호면 AccessDeniedException(또는 커스텀) 던져서 403

        MyOrderDetailDto myOrderDetail = myOrderService.getMyOrderDetail(orderNo, sessionUser.getMemberId());

        model.addAttribute("order", myOrderDetail);
        addContentView(model, "shop/my/order/detail :: content");

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
