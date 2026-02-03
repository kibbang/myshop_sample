package sample.myshop.shop.main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sample.myshop.shop.main.dto.MainProductCardDto;
import sample.myshop.shop.main.service.MainService;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ShopMainController {

    private final MainService mainService;

    @GetMapping("/")
    public String index(Model model) {

        List<MainProductCardDto> productCards = mainService.getProductCards(12);

        model.addAttribute("content", "shop/main/index :: content");
        model.addAttribute("cards", productCards);

        return "shop/layout/base";

    }
}
