package sample.myshop.admin.order.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
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
