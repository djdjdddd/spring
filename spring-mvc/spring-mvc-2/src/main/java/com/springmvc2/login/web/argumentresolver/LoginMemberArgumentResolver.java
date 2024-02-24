package com.springmvc2.login.web.argumentresolver;

import com.springmvc2.login.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Member;

/**
 * @author : yong
 * @fileName : LoginMemberArgumentResolver
 * @date : 2024-02-25
 * @description :
 */
@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    // ArgumentResolver 효용 : 공통 작업이 필요할 때 컨트롤러를 더욱 편리하게 사용할 수 있다.
    // ArgumentResolver 예시 : @Login 어노테이션 참조 (HomeController)

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");

        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());

        return hasLoginAnnotation && hasMemberType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        log.info("resolverArgument 실행");

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null; // 1. 세션이 null이면 Model 객체에 null을 넣겠다는 의미
        }

        return session.getAttribute(SessionConst.LOGIN_MEMBER); // 2. 세션이 null이 아니면 Model 객체에 로그인한 멤버 정보를 넣겠다는 의미
    }

    // 이렇게 한다고 해서 끝난게 아니다.
    // WebMvcConfigurer 인터페이스 구현체에서 addArgumentResolvers 메서드를 오버라이드하여 ArgumentResolver를 등록해야 한다.

}
