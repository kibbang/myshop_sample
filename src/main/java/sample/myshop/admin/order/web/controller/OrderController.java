package sample.myshop.admin.order.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sample.myshop.admin.order.domain.dto.web.OrderDetailDto;
import sample.myshop.admin.order.domain.dto.web.OrderListItemDto;
import sample.myshop.admin.order.domain.dto.web.OrderSearchConditionDto;
import sample.myshop.order.service.OrderService;

import java.util.List;

import static java.lang.Math.max;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public String orders(@ModelAttribute(name = "searchForm") OrderSearchConditionDto searchForm, Model model) {
        List<OrderListItemDto> orderList = orderService.searchOrders(searchForm, max(searchForm.getPage(), 1), searchForm.getSize());
        Long totalOrderCount = orderService.getTotalOrderCount(searchForm);

        model.addAttribute("orderList", orderList);
        model.addAttribute("totalOrderCount", totalOrderCount);

        addContentView(model, "admin/order/list :: content");
        return "admin/layout/base";
    }

    @GetMapping("/{orderId}")
    public String show(@PathVariable Long orderId, Model model) {
        OrderDetailDto order = orderService.getOrder(orderId);

        model.addAttribute("order", order);
        addContentView(model, "admin/order/detail :: content");

        return "admin/layout/base";
    }

    @PostMapping("/{orderId}/cancel")
    public String cancel(@PathVariable Long orderId, RedirectAttributes redirectAttributes) {
        try {
            orderService.cancelOrder(orderId);

            redirectAttributes.addFlashAttribute("message", "주문이 취소되었습니다.");
        } catch (Exception e) {
            log.error("주문 취소 실패: orderId={}", orderId, e);
            redirectAttributes.addFlashAttribute("error", "주문 취소에 실패했습니다.");
        }

        return "redirect:/admin/orders/" + orderId;
    }

    /**
     * 뷰에 컨텐츠 삽입
     * @param model
     * @param attributeTarget
     */
    private static void addContentView(Model model, String attributeTarget) {
        model.addAttribute("content", attributeTarget);
        model.addAttribute("activeMenu", "products");
    }
}
