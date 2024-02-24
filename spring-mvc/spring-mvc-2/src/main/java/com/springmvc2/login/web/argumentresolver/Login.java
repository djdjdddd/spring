package com.springmvc2.login.web.argumentresolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : yong
 * @fileName : Login
 * @date : 2024-02-25
 * @description : 커스텀 어노테이션
 */
@Target(ElementType.PARAMETER)      // 파라미터에만 사용하겠다는 의미
@Retention(RetentionPolicy.RUNTIME) // 리플렉션 등을 활용할 수 있도록 런타임까지 어노테이션 정보가 남아있게 함
public @interface Login {
}
