package sample.myshop.admin.member.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sample.myshop.admin.member.domain.dto.web.AdminMemberListDto;
import sample.myshop.admin.member.domain.dto.web.AdminMemberSearchConditionDto;
import sample.myshop.admin.member.domain.dto.web.AdminMemberUpdateDto;
import sample.myshop.admin.member.domain.dto.web.AdminMemberUpdateRequestDto;
import sample.myshop.admin.member.service.AdminMemberService;
import sample.myshop.shop.my.domain.dto.AdminMemberDetailDto;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @GetMapping
    public String members(@ModelAttribute(name = "searchForm") AdminMemberSearchConditionDto searchForm, Model model) {

        List<AdminMemberListDto> members = adminMemberService.searchMembers(searchForm);
        int totalCount = adminMemberService.getTotalCount(searchForm);

        model.addAttribute("members", members);
        model.addAttribute("totalCount", totalCount);
        addContentView(model, "admin/member/list :: content");
        return "admin/layout/base";
    }

    @GetMapping("/{memberId}")
    public String show(@PathVariable Long memberId, Model model) {

        AdminMemberDetailDto memberDetail = adminMemberService.getMemberDetail(memberId);

        model.addAttribute("member", memberDetail);
        addContentView(model, "admin/member/detail :: content");

        return "admin/layout/base";
    }

    @GetMapping("/{memberId}/edit")
    public String edit(
            @PathVariable Long memberId,
            @ModelAttribute(name = "form") AdminMemberUpdateRequestDto form,
            Model model
    ) {
        AdminMemberDetailDto member = adminMemberService.getMemberDetail(memberId);

        form.setName(member.getName());
        form.setPhone(member.getPhone());
        form.setZipCode(member.getZipCode());
        form.setBaseAddress(member.getBaseAddress());
        form.setDetailAddress(member.getDetailAddress());
        form.setActive(member.getActive());

        model.addAttribute("member", member);
        addContentView(model, "admin/member/edit :: content");
        return "admin/layout/base";
    }

    @PostMapping("/{memberId}")
    public String update(
            @PathVariable Long memberId,
            @Validated @ModelAttribute(name = "form") AdminMemberUpdateRequestDto form,
            Model model,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            AdminMemberDetailDto adminMemberDetailDto = adminMemberService.getMemberDetail(memberId);
            model.addAttribute("member", adminMemberDetailDto);
            model.addAttribute("memberId", memberId);

            addContentView(model, "admin/member/edit :: content");

            return "admin/layout/base";
        }

        AdminMemberUpdateDto adminMemberUpdateDto = new AdminMemberUpdateDto(
                memberId,
                form.getName(),
                form.getPhone(),
                form.getZipCode(),
                form.getBaseAddress(),
                form.getDetailAddress(),
                form.getActive()
        );

        adminMemberService.updateMember(adminMemberUpdateDto);

        redirectAttributes.addFlashAttribute("flashMessage", "회원 수정이 완료되었습니다.");

        return "redirect:/admin/members/" + memberId;
    }

    /**
     * 뷰에 컨텐츠 삽입
     * @param model
     * @param attributeTarget
     */
    private static void addContentView(Model model, String attributeTarget) {
        model.addAttribute("content", attributeTarget);
        model.addAttribute("activeMenu", "members");
    }
}
