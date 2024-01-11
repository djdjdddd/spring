package com.springmvc2.login.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter { // Filter(인터페이스)를 implements

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter info");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("log filter doFilter");

        // 타입 캐스팅하는 이유 : ServletRequest는 HttpServletRequest의 부모인데, Http에 비해 별 기능이 없다. 그래서 '다운 캐스팅' 해주었다.
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        String uuid = UUID.randomUUID().toString(); // request를 서로 구분해주기 위해 UUID를 이용하겠다고 함. (영한킴)

        try {
            log.info("REQUEST [{}][{}]", uuid, requestURI);
            chain.doFilter(request, response); // ★ doFilter : (1)다음 filter가 있으면 filter를 실행하고, (2)없으면 Servlet을 실행한다.
                                               // 즉, "HTTP 요청 -> WAS -> 필터 -> (필터) -> 서블릿 -> 컨트롤러" 이 흐름을 따라가도록 하는 메서드라는 뜻이다.
                                                // 즉, doFilter를 하지 않으면 아예 다음 단계로 넘어가지 못한다.
        }catch (Exception e){
            throw e;
        }finally {
            log.info("RESPONSE [{}][{}]", uuid, requestURI);
        }

        // ★★ 중요
        // 이렇게 클래스(LogFilter)를 만든다고 끝이 아니다.
        // 반드시 등록을 해줘야 이 Filter가 제대로 작동한다.
        // WebConfig 클래스 참조

    }

    @Override
    public void destroy() {
        log.info("log filter destroy");
    }
}
