package sample.myshop.member.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sample.myshop.member.domain.dto.MemberRegisterFormDto;
import sample.myshop.member.service.MemberService;

@Controller
@Slf4j
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/join")
    public String registerForm(@ModelAttribute("memberRegisterForm") MemberRegisterFormDto form, Model model) {
        addContentView(model, "shop/member/join :: content");

        return "shop/layout/base";
    }

    @PostMapping("/join")
    public String register(@Validated @ModelAttribute(name = "memberRegisterForm") MemberRegisterFormDto form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            addContentView(model, "shop/member/join :: content");

            return "shop/layout/base";
        }

        memberService.join(form);

        return "redirect:/login";
    }

    /**
     * 뷰에 컨텐츠 삽입
     *
     * @param model
     * @param attributeTarget
     */
    private static void addContentView(Model model, String attributeTarget) {
        model.addAttribute("content", attributeTarget);
    }
}
