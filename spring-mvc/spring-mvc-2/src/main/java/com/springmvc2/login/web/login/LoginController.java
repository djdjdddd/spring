package com.springmvc2.login.web.login;

import com.springmvc2.login.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Member;

/**
 * @author : yong
 * @fileName : LoginController
 * @date : 2024-02-24
 * @description :
 */
@Controller
@Slf4j
public class LoginController {

    // redirectURL 에 준 값을 RequestParam 으로 받고, 이를 리다이렉트 하는데 사용한다.
//    @PostMapping("/login")
//    public String loginV4(@Valid @ModelAttribute LoginForm form
//                          , BindingResult bindingResult
//                          , @RequestParam(defaultValue = "/") String redirectURL
//                          , HttpServletRequest request){
//        if(bindingResult.hasErrors()){
//            return "login/loginForm";
//        }
//
//        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
//
//        if(loginMember == null){
//            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
//            return "login/loginForm";
//        }
//
//        // 로그인 성공 처리
//        // 세션이 있으면 그 세션 반환, 없으면 신규 세션 생성
//        HttpSession session = request.getSession();
//        // 세션에 로그인 회원 정보 보관
//        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
//
//        return "redirect:" + redirectURL;
//    }

}
