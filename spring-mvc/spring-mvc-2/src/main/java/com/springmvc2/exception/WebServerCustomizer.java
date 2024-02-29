package com.springmvc2.exception;

import org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @author : yong
 * @fileName : WebServerCustomizer
 * @date : 2024-03-01
 * @description :
 */
@Component
public class WebServerCustomizer implements WebServerFactoryCustomizer<ConfigurableJettyWebServerFactory> {

    @Override
    public void customize(ConfigurableJettyWebServerFactory factory) {
        // 404 에러가 발생하면 path 에 해당하는 컨트롤러 실행
        ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "error-page/404");

        // 500 에러가 발생하면 path 에 해당하는 컨트롤러 실행
        ErrorPage errorPage500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "error-page/500");

        // RuntimeException 이 발생하면 path 에 해당하는 컨트롤러 실행
        ErrorPage errorPageEx = new ErrorPage(RuntimeException.class, "error-page/500"); // RuntimeException 뿐만 아니라 자식 Exception 까지 처리한다고 함.

        // ErrorPage 등록
        factory.addErrorPages(errorPage404, errorPage500, errorPageEx);
    }

}
