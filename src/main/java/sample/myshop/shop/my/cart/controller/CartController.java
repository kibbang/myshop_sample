package sample.myshop.shop.my.cart.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sample.myshop.auth.LoginUser;
import sample.myshop.auth.SessionUser;
import sample.myshop.shop.my.cart.domain.dto.CartAddRequestDto;
import sample.myshop.shop.my.cart.domain.dto.CartChangeQuantityRequestDto;
import sample.myshop.shop.my.cart.domain.dto.CartListDto;
import sample.myshop.shop.my.cart.service.CartService;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/my/cart")
public class CartController {
    private final CartService cartService;

    @PostMapping("/items")
    public String addItem(
            @Validated @ModelAttribute("cartForm") CartAddRequestDto form,
            BindingResult bindingResult,
            @LoginUser SessionUser sessionUser,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "장바구니 담기 요청이 올바르지 않습니다.");
            return "redirect:/my/cart";
        }

        cartService.addItem(session, sessionUser, form);

        redirectAttributes.addFlashAttribute("successMessage", "장바구니에 담았습니다.");

        return "redirect:/my/cart";
    }

    @GetMapping
    public String cartPage(@LoginUser SessionUser sessionUser, HttpSession session, Model model) {
        CartListDto cart = cartService.getCartView(session, sessionUser);
        model.addAttribute("cart", cart);

        model.addAttribute("content", "shop/my/cart/index :: content");

        return "shop/layout/base";
    }

    @PostMapping("/items/{cartItemId}/quantity")
    public String changeQuantity(
            @PathVariable Long cartItemId,
            @Validated @ModelAttribute("qtyForm") CartChangeQuantityRequestDto form,
            BindingResult bindingResult,
            @LoginUser SessionUser sessionUser,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "수량은 1개 이상이어야 합니다.");
            return "redirect:/my/cart";
        }

        cartService.changeQuantity(session, sessionUser, cartItemId, form);
        redirectAttributes.addFlashAttribute("successMessage", "수량을 변경했습니다.");
        return "redirect:/my/cart";
    }

    @PostMapping("/items/{cartItemId}/remove")
    public String removeItem(
            @PathVariable Long cartItemId,
            @LoginUser SessionUser sessionUser,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        cartService.removeItem(session, sessionUser, cartItemId);
        redirectAttributes.addFlashAttribute("successMessage", "장바구니에서 삭제했습니다.");
        return "redirect:/my/cart";
    }
}
