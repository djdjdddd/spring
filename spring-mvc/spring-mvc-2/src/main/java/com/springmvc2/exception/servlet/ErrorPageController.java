package com.springmvc2.exception.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : yong
 * @fileName : ErrorPageController
 * @date : 2024-03-01
 * @description : 오류 처리 컨트롤러
 */
@Slf4j
@Controller
public class ErrorPageController {

    // 404
    @RequestMapping("/error-page/404")
    public String errorPage404(HttpServletRequest request, HttpServletResponse response){
        log.info("errorPage 404");
        return "error-page/404";
    }

    // 500
    @RequestMapping("/error-page/500")
    public String errorPage500(HttpServletRequest request, HttpServletResponse response){
        log.info("errorPage 500");
        return "error-page/500";
    }

    // ex
    @RequestMapping("/error-page/ex")
    public String errorPageEx(HttpServletRequest request, HttpServletResponse response){
        log.info("errorPage ex");
        return "error-page/500";
    }

}
