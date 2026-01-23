package sample.myshop.common.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import sample.myshop.utils.Csrf;

import static sample.myshop.auth.SessionConst.*;

@ControllerAdvice
public class CsrfModelAdvice {
    @ModelAttribute(CSRF_PARAM)
    public String csrfToken(HttpServletRequest request) {

        HttpSession session = request.getSession(true);

        return Csrf.generateToken(session);
    }
}
