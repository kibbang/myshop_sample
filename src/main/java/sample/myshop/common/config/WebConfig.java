package sample.myshop.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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
                .addPathPatterns(
                        "/orders/**",
                        "/my/**"
                )
                .excludePathPatterns(
                        "/",
                        "/products/**",
                        "/members/join",
                        "/login",
                        "/logout",
                        "/*.ico",
                        "/error",
                        "/assets/**",
                        "/.well-known/**",
                        "/uploads/**",
                        "/api/**"
                );

        registry.addInterceptor(new AdminInterceptor())
                .order(2)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/error",
                        "/assets/**",
                        "/.well-known/**",
                        "/uploads/**"

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

    // 스프링은 기본적으로 프로젝트 루트의 ./uploads 폴더를 자동으로 정적 리소스로 서빙하지 않기때문에 핸들러에 연결해줘야함
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:./uploads/");
    }
}
