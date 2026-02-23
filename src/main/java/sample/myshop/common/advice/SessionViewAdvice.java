package sample.myshop.common.advice;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import sample.myshop.auth.SessionConst;
import sample.myshop.auth.SessionUser;

@Slf4j
@ControllerAdvice
public class SessionViewAdvice {
    @ModelAttribute("sessionView")
    public SessionView sessionView(HttpSession session) {
        SessionUser sessionUser = (session == null) ? null : (SessionUser) session.getAttribute(SessionConst.LOGIN_USER);
        return new SessionView(sessionUser);
    }

    public record SessionView(SessionUser sessionUser) {
    }
}
