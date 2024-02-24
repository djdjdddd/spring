package com.springmvc2.login.web.filter;

import com.springmvc2.login.web.SessionConst;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;

/**
 * @author : yong
 * @fileName : LoginCheckFilter
 * @date : 2024-02-24
 * @description :
 */
@Slf4j
public class LoginCheckFilter implements Filter {

    private static final String[] whitelist = {"/", "members/add", "login", "logout", "/css/*"};

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        try {
            log.info("인증 체크 필터 시작 {}", requestURI);
            if(isLoginCheckPath(requestURI)){
                log.info("인증 체크 로직 실행 {}", requestURI);
                HttpSession session = httpRequest.getSession(false);// 세션을 얻되 현재 세션이 존재하지 않으면 새로 생성하지 않고 null을 리턴한다. (구현체 메서드를 까보면 알 수 있다)
                if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null){
                    log.info("미인증 사용자 요청 {}", requestURI);
                    // 로그인으로 redirect
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI); // 로그인 후 클라이언트가 원래 요청했던 해당 URI로 다시 리다이렉트 시켜주기 위함. (LoginController의 loginV4 메서드 참조)
                    return; // 여기가 중요! 미인증 사용자는 다음으로 진행하지 않고 로직 수행을 끝낸다!
                }
            }
            filterChain.doFilter(servletRequest, httpResponse);
        }catch (Exception e){
            throw e; // 예외 로깅 가능하지만, throw 하여 WAS(톰캣)까지 예외를 보내주어야 함.
        }finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
    }

    /**
     * 화이트 리스트의 경우 인증 체크 X
     */
    private boolean isLoginCheckPath(String requestURI){
        // requestURI 가 화이트 리스트와 매치하는지 체크한다.
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}
