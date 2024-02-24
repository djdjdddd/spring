package com.springmvc2.login.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

/**
 * @author : yong
 * @fileName : LogFilter2
 * @date : 2024-02-24
 * @description :
 */
@Slf4j
public class LogFilter2 implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // ServletRequest 는 HttpServletRequest 의 부모이다.
        // cf. ServletRequest 는 HTTP 요청이 아닌 경우까지 고려해서 만든 인터페이스다. HTTP를 사용하면 아래 코드처럼 다운캐스팅해서 사용하면 된다.
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest; // ServletRequest 를 HttpServletRequest 로 다운캐스팅(부모 -> 자식 타입)
        String requestURI = httpRequest.getRequestURI(); // HTTP 요청의 URI 를 얻는다.

        String uuid = UUID.randomUUID().toString(); // 사용자 요청을 구분하기 위한 랜덤 값

        try {
            log.info("REQUEST [{}][{}]", uuid, requestURI);
            filterChain.doFilter(servletRequest, servletResponse); // ★반드시 필요 : (다음 필터가 있으면)필터를 호출고, (다음 필터가 없으면)서블릿을 호출한다.
        } catch (Exception e){
            throw e;
        }finally {
            log.info("RESPONSE [{}][{}]", uuid, requestURI);
        }
    }

    // 이렇게 필터 로직 구현체(Filter 인터페이스 구현체)를 만들기만 해서는 필터가 적용되지 않는다.
    // WebConfig 참조 : @Configuration 에 FilterRegistrationBean 을 추가한 것처럼 필터를 등록 및 설정해줘야 한다.

    // 필터를 등록하는 방법은 여러가지가 있지만, 스프링 부트를 사용한다면 `FilterRegistrationBean`을 사용해서 등록하면 된다.
}
