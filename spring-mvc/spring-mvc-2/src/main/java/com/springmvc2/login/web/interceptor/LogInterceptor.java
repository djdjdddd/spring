package com.springmvc2.login.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

/**
 * @author : yong
 * @fileName : LogInterceptor
 * @date : 2024-02-25
 * @description :
 */
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    private static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        // 필터에서 했던 것처럼 로그를 찍는 방법
        // afterCompletion 에서 찍을 생각이므로 request에 setAttribute하여 해당 uuid 값을 넘겨주는 방식을 채택
        request.setAttribute(LOG_ID, uuid);

        // HandlerMethod 타입 : @RequestMapping
        // ResourceHttpRequestHandler : 정적 리소스
        if(handler instanceof HandlerMethod){ // ★즉 이 코드의 의미는 해당 handler가 컨트롤러에서 @RequestMapping 어노테이션이 달린 메서드인지 확인하기 위한 용도이다.
            // hm : 호출할 컨트롤러 메서드의 모든 정보가 포함되어 있다.
            HandlerMethod hm = (HandlerMethod)handler;
        }

        log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler);
        return true; // preHandle 은 리턴 타입이 boolean 이란 걸 잊지 말길 바란다.
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHanlde [{}]", modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = (String) request.getAttribute(LOG_ID);

        log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler);

        // 예외가 발생하지 않으면 null 로 온다.
        if(ex != null){
            log.error("afterCompletion error!!", ex);
        }
    }

    // 인터셉터를 등록하는 방법은 필터와 조금 다르다. (WebConfig 클래스 참조)
    // 1. WebMvcConfigurer 를 implements 해야 하고
    // 2. addInterceptors 라는 메서드를 오버라이드 하여 등록하면 된다.

}
