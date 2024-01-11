package com.springmvc2.login;

import com.springmvc2.login.web.filter.LogFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class WebConfig {

    // 스프링 부트가 WAS를 띄우기 때문에...
    // 이렇게 '필터 등록 빈(FilterRegistrationBean)'에 내가 만든 필터를 등록해주면 된다.
    @Bean
    public FilterRegistrationBean logFilter(){
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();

        filterRegistrationBean.setFilter(new LogFilter());  // 내가 만든 LogFilter 등록
        filterRegistrationBean.setOrder(1);                 // 순서
        filterRegistrationBean.setUrlPatterns(Collections.singleton("/*"));        // URL 패턴

        return filterRegistrationBean;
    }
}
