package sample.myshop.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import sample.myshop.auth.SessionConst;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // 주문 준비는 비로그인도 허용 (여기서 ORDER_PREPARE를 세션에 저장해야 함)
        if ("/orders/prepare".equals(requestURI) && ("POST".equalsIgnoreCase(method))) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SessionConst.LOGIN_USER) == null) {
            response.sendRedirect("/login?redirect=" + requestURI);
            return false;
        }

        return true;
    }
}
