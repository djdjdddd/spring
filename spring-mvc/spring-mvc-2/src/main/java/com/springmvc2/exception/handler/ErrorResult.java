package com.springmvc2.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * API 예외 발생시 클라이언트 측에 보여줄 ErrorResult 객체로 에러 코드와 에러 메시지 값을 갖는다.
 */
@Data
@AllArgsConstructor
public class ErrorResult {

	private String code;		// 에러 코드
	private String message;		// 에러 메시지

}
