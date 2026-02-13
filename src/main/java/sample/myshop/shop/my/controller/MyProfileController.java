package sample.myshop.shop.my.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sample.myshop.auth.SessionUser;
import sample.myshop.shop.my.domain.dto.MyProfileUpdateRequestDto;

@Controller
@RequestMapping("/my/profile")
public class MyProfileController {
    @GetMapping
    public String profile(@ModelAttribute(name = "form") MyProfileUpdateRequestDto form, Model model) {

        addContentView(model, "shop/my/profile/edit :: content");

        return "shop/layout/base";
    }

    @PostMapping
    public String update() {
        return "redirect:/my/profile";
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
