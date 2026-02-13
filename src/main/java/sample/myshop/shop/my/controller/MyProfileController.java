package sample.myshop.shop.my.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sample.myshop.auth.LoginUser;
import sample.myshop.auth.SessionUser;
import sample.myshop.member.domain.Address;
import sample.myshop.member.domain.Member;
import sample.myshop.member.service.MemberService;
import sample.myshop.shop.my.domain.dto.MyProfileUpdateDto;
import sample.myshop.shop.my.domain.dto.MyProfileUpdateRequestDto;

@Controller
@RequestMapping("/my/profile")
@RequiredArgsConstructor
public class MyProfileController {

    private final MemberService memberService;

    @GetMapping
    public String profile(@ModelAttribute(name = "form") MyProfileUpdateRequestDto form, Model model, @LoginUser SessionUser sessionUser) {

        Member member = memberService.findMemberById(sessionUser.getMemberId());
        Address memberAddress = member.getAddress();

        form.setName(member.getName());
        form.setPhone(member.getPhone());
        form.setZipCode(memberAddress.getZipcode());
        form.setBaseAddress(memberAddress.getBaseAddress());
        form.setDetailAddress(memberAddress.getDetailAddress());

        addContentView(model, "shop/my/profile/edit :: content");

        return "shop/layout/base";
    }

    @PostMapping
    public String update(
            @Validated @ModelAttribute(name = "form") MyProfileUpdateRequestDto form,
            BindingResult bindingResult,
            Model model,
            @LoginUser SessionUser sessionUser,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            addContentView(model, "shop/my/profile/edit :: content");
            return "shop/layout/base";
        }

        MyProfileUpdateDto myProfileUpdateDto = new MyProfileUpdateDto(
                sessionUser.getMemberId(),
                form.getName(),
                form.getPhone(),
                form.getZipCode(),
                form.getBaseAddress(),
                form.getDetailAddress()
        );

        memberService.updateInfo(myProfileUpdateDto);

        redirectAttributes.addFlashAttribute("message", "정보가 수정되었습니다.");

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
