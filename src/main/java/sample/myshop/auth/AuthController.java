package sample.myshop.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sample.myshop.member.enums.Role;
import sample.myshop.member.service.MemberService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static sample.myshop.auth.SessionConst.*;

@Slf4j
@Controller
public class AuthController {

    private final MemberService memberService;

    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String loginView(
            @ModelAttribute LoginForm loginForm,
            Model model,
            @RequestParam(required = false) String redirect,
            @LoginUser SessionUser loginUser
    ) {

        if (loginUser != null) {
            String target = sanitizeRedirect(redirect);

            if (target != null) {
                return "redirect:" + target;
            }

            if (loginUser.getRole() == Role.ADMIN) {
                return "redirect:/admin";
            }

            return "redirect:/";
        }

        model.addAttribute("content", "shop/auth/login :: content");
        model.addAttribute("redirect", redirect);

        return "shop/layout/base";
    }

    @PostMapping("/login")
    public String loginProcess(
            @ModelAttribute("loginForm") LoginForm loginForm,
            @RequestParam(required = false) String redirect,
            HttpServletRequest request,
            Model model
    ) {
        String loginId = loginForm.getLoginId();
        String password = loginForm.getPassword();

        try {
            SessionUser loggedInUser = memberService.login(loginId, password);

            HttpSession newSession = request.getSession(true);

            request.changeSessionId(); // 세션 ID 변경 (기존엔 새로 발급하였으나 주문 전 로그인으로 왔을경우 정보를 가지고 있어야하기에)

            newSession.setAttribute(LOGIN_USER, loggedInUser);

            if (redirect == null || redirect.isBlank()) {
                if (Role.ADMIN.equals(loggedInUser.getRole())) {
                    log.info("redirect to admin page");
                    return "redirect:/admin";
                }

                return "redirect:/";
            }

            String decodedRedirect = URLDecoder.decode(redirect, StandardCharsets.UTF_8);

            if (!decodedRedirect.startsWith("/") || decodedRedirect.startsWith("//") || decodedRedirect.contains("\\")) {
                decodedRedirect = "/";
            }

            // 관리자로 로그인 할 경우 redirect URL 여부와 관계 없이 /admin으로
            if (Role.ADMIN.equals(loggedInUser.getRole())) {
                return "redirect:/admin";
            }

            return "redirect:" + decodedRedirect;

        } catch (IllegalArgumentException e) {
            model.addAttribute("loginError", true);
            model.addAttribute("content", "shop/auth/login :: content");
            model.addAttribute("redirect", redirect);
            model.addAttribute("error", e.getMessage());
            return "shop/layout/base";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }

    private String sanitizeRedirect(String redirect) {
        if (redirect == null || redirect.isBlank()) return null;

        String decoded = URLDecoder.decode(redirect, StandardCharsets.UTF_8);

        if (!decoded.startsWith("/")) return null;
        if (decoded.startsWith("//")) return null;
        if (decoded.contains("\\")) return null;
        if (decoded.contains("://")) return null;

        return decoded;
    }
}
