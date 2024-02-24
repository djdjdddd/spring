package com.springmvc2.login.web.interceptor;

import com.springmvc2.login.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author : yong
 * @fileName : LoginCheckInterceptor
 * @date : 2024-02-25
 * @description :
 */
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    // 왜 인터셉터가 필터에 비해 훨씬 편한지 느낄 수 있다. (by 센세)
    // 필터와 달리 화이트리스트를 체크하는 로직이 빠져있다. 왜냐하면 그 로직을 인터셉터 등록(WebConfig)하는 곳에서 설정할 수 있기 때문이다.

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("인증 체크 인터셉터 실행 {}", requestURI);

        HttpSession session = request.getSession();
        if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null){
            log.info("미인증 사용자 요청");
            // 로그인으로 redirect
            response.sendRedirect("/login?redirectURL=" + requestURI);
            return false; // false : 더이상 진행하지 않는다.
        }

        return true;
    }
}
