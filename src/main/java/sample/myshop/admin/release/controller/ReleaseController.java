package sample.myshop.admin.release.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sample.myshop.admin.release.service.ReleaseService;
import sample.myshop.release.enums.ReleaseStatus;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/releases")
public class ReleaseController {

    private final ReleaseService releaseService;

    @PostMapping("/{orderId}/release")
    public String toChangeStatus(@PathVariable Long orderId, RedirectAttributes redirectAttributes) {
        log.info("toChangeStatus orderId={}", orderId);

        releaseService.toRelease(orderId);

        redirectAttributes.addFlashAttribute(
                "message",
                "출고 상태 변경: " + ReleaseStatus.RELEASED.getLabel()
        );

        return "redirect:/admin/orders/" + orderId;
    }
}
