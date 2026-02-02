package sample.myshop.shop.main;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class ShopMainController {
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("content", "shop/main/index :: content");
        return "shop/layout/base";

    }
}
