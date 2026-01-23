package sample.myshop.argumentresolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import sample.myshop.auth.LoginUser;
import sample.myshop.auth.SessionConst;
import sample.myshop.auth.SessionUser;

public class LoginArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasParameterAnnotation = parameter.hasParameterAnnotation(LoginUser.class);
        boolean isAssignableFrom = SessionUser.class.isAssignableFrom(parameter.getParameterType());

        return hasParameterAnnotation && isAssignableFrom;
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();

        HttpSession session = request.getSession(false);

        SessionUser sessionUser = (SessionUser) session.getAttribute(SessionConst.LOGIN_USER);

        LoginUser parameterAnnotation = parameter.getParameterAnnotation(LoginUser.class);
        boolean required = parameterAnnotation != null && parameterAnnotation.required();

        if (required && sessionUser == null) {
            throw new MissingRequestValueException("로그인이 필요합니다");
        }

        return sessionUser;
    }
}
