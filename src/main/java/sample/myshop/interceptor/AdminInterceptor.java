package sample.myshop.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import sample.myshop.auth.SessionUser;

import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.*;
import static sample.myshop.auth.SessionConst.*;
import static sample.myshop.enums.auth.UserType.ADMIN;

public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        SessionUser loginUser = (session == null)
                ? null
                : (SessionUser) session.getAttribute(LOGIN_USER);


        /** 세션이 없는 경우 */
        if (loginUser == null) {
            // URI 조립
            String requestURI = request.getRequestURI();
            String queryString = request.getQueryString();
            String fullRequestUri = queryString == null ? requestURI : (requestURI + "?" + queryString);

            String encodedUri = URLEncoder.encode(fullRequestUri, UTF_8);
            response.sendRedirect("/login?redirect=" + encodedUri);
            return false;
        }

        /** 어드민이 아닌 경우 */
        if (!ADMIN.equals(loginUser.getRole())) {
            response.sendRedirect("/");
            return false;
        }

        return true;
    }
}
