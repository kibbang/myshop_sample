package sample.myshop.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sample.myshop.argumentresolver.LoginArgumentResolver;
import sample.myshop.interceptor.AdminInterceptor;
import sample.myshop.interceptor.CsrfInterceptor;
import sample.myshop.interceptor.LoginCheckInterceptor;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",
                        "/products/**",
                        "/members/add",
                        "/login",
                        "/logout",
                        "/*.ico",
                        "/error",
                        "/assets/**",
                        "/.well-known/**"
                );

        registry.addInterceptor(new AdminInterceptor())
                .order(2)
                .addPathPatterns("/admin/**").excludePathPatterns(
                        "/error",
                        "/assets/**",
                        "/.well-known/**"
                );

        registry.addInterceptor(new CsrfInterceptor())
                .order(0)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/*.ico",
                        "/error",
                        "/assets/**",
                        "/.well-known/**"
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginArgumentResolver());
    }
}
