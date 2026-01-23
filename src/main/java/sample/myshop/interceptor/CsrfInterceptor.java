package sample.myshop.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import static sample.myshop.auth.SessionConst.*;

public class CsrfInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();

        boolean unsafe = "POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method) || "PATCH".equals(method);

        if (!unsafe) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        String expectedToken = (String) session.getAttribute(CSRF_TOKEN);

        String actualToken = request.getParameter(CSRF_PARAM);

        if (actualToken == null || actualToken.isBlank()) {
            actualToken = request.getHeader("X-CSRF-TOKEN");
        }

        if (expectedToken == null || !expectedToken.equals(actualToken)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;

    }
}
