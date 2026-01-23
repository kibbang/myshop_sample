package sample.myshop.admin.account.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminTestController {

    @RequestMapping("/admin/test")
    public String test(Model model) {
        model.addAttribute("title", "상품 목록");
        model.addAttribute("content", "admin/product/list :: content");
        return "admin/layout/base";
    }
}
