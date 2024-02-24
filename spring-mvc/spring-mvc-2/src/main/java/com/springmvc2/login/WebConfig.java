package com.springmvc2.login;

import com.springmvc2.login.web.argumentresolver.Login;
import com.springmvc2.login.web.argumentresolver.LoginMemberArgumentResolver;
import com.springmvc2.login.web.filter.LogFilter;
import com.springmvc2.login.web.filter.LoginCheckFilter;
import com.springmvc2.login.web.interceptor.LogInterceptor;
import com.springmvc2.login.web.interceptor.LoginCheckInterceptor;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // ArgumentResolver 등록
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }

    // 인터셉터 등록
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())                   // 등록할 인터셉터
                .order(1)                                               // 순서
                .addPathPatterns("/**")                                 // 적용할 URL 패턴 (필터와 달리 *을 2개 쓴다)
                .excludePathPatterns("/css/**", "/*.ico", "/error");    // 제외할 URL 패턴 ★

        // 필터와 비교해보면 인터셉터는 addPathPatterns, excludePathPatterns 로 매우 정밀하게 URL 패턴을 지정할 수 있다.

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/members/add", "login", "logout", "/css/**", "/*.ico", "error");
    }

    // 스프링 부트가 WAS를 띄우기 때문에...
    // 이렇게 '필터 등록 빈(FilterRegistrationBean)'에 내가 만든 필터를 등록해주면 된다.

    // 로그 필터
    @Bean
    public FilterRegistrationBean logFilter(){
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();

        filterRegistrationBean.setFilter(new LogFilter());  // 등록할 필터를 지정한다.
        filterRegistrationBean.setOrder(1);                 // 순서를 지정한다.
        filterRegistrationBean.addUrlPatterns("/*");        // URL 패턴을 지정한다.
                                                            // "/*" 모든 URL 패턴에 필터 적용하겠다는 의미
//        filterRegistrationBean.setUrlPatterns(Collections.singleton("/*"));        // URL 패턴

        return filterRegistrationBean;
    }

    // 로그인 체크 필터
    @Bean
    public FilterRegistrationBean loginCheckFilter(){
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();

        filterRegistrationBean.setFilter(new LoginCheckFilter());
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.addUrlPatterns("/*");
        // cf. addUrlPatterns("/*") 를 준 이유
        // 물론 로그인 체크를 적용할 URL을 일일이 지정해도 되긴 한다.
        // 그러나 현재 방식처럼 모든 URL에 적용하고, 적용하지 않을 일부 패턴(화이트리스트)만 제외시키는 것이 더 편하고 깔끔하다.

        return filterRegistrationBean;
    }
}
