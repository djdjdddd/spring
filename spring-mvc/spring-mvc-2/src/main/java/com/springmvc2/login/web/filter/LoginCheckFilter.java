package com.springmvc2.login.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;
import java.util.function.Predicate;

@Slf4j
public class LoginCheckFilter implements Filter {

    private static final String[] whitelist = {"/", "/members/add", "/login", "/logout", "/css/*"}; // 로그인 안했다고 해서 CSS 파일 요청까지 막아버리면 안되겠죠? ㅎㅎ..

    // cf. init(), destroy()는 인터페이스의 디폴트 메서드이기 때문에 굳이 구현할 필요 없다. (그리고 Filter 클래스 생성할 때마다 매번 그렇게 초기화/삭제 할 필요 없기도 하고...)

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 우선 request, response 둘 다 타입 캐스팅 (HTTP 요청 전문 타입임. 일반 ServletReq, Res는 HTTP 요청 외에 다른 것도 받을 수 있게 설계돼있다고 함)
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            log.info("인증 체크 필터 시작 {}", requestURI);

            if(isLoginCheckPath(requestURI)){
                log.info("인증 체크 로직 실행 {}", requestURI);
                HttpSession session = httpRequest.getSession(false);
                if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null){
                    log.info("미인증 사용자 요청 {}", requestURI);
                }
            }
        }catch ()
    }

    /**
     * 화이트 리스트의 경우 인증 체크 X
     */
    private boolean isLoginCheckPath(String requestURI){
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI); // PatternMatchUtils : 스프링이 제공하는 Util. 둘 간의 패턴이 매칭되는지 체크해준다. 이걸 이용하여 편하게 코드를 짜도록 하자
    }
}
