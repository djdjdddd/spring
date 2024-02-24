package com.springmvc2.login.web;

import com.springmvc2.login.web.argumentresolver.Login;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.lang.reflect.Member;

/**
 * @author : yong
 * @fileName : HomeController
 * @date : 2024-02-25
 * @description :
 */
public class HomeController {

    @GetMapping("/")
    public String homeLoginV3ArgumentResolver(@Login Member loginMember, Model model){

        // 세션에 회원 데이터가 없으면 home
        if(loginMember == null){
            return "home";
        }

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

}
