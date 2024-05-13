package com.springmvc2.exception.api;

import com.springmvc2.exception.handler.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class ApiExceptionV2Controller {

	// 현재 컨트롤러에서 IAE가 발생할 경우 해당 예외를 잡아 처리한다.
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)	// 따로 HTTP 상태코드를 정의했다. 왜냐하면 예외를 처리함에 따라 정상(코드 200) 흐름이 되기 때문이다.
	public ErrorResult IaeHandle(IllegalArgumentException e){
		log.error("[exceptionHandler ex]", e);					// 단순히 어떤 Ex가 발생했는지 로그를 찍기 위한 코드
		return new ErrorResult("BAD", e.getMessage());	// 에러 코드와 메시지를 담은 객체를 반환한다.
	}

	// 현재 컨트롤러에서 RuntimeException이 발생할 경우 해당 예외를 잡아 처리한다.
	@ExceptionHandler	// 예외 종류 생략 가능
	public ResponseEntity<ErrorResult> runtimeExHandle(RuntimeException e){
		ErrorResult errorResult = new ErrorResult("run", e.getMessage());
		return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
	}

	// 가장 상위 예외 계층인 Exception이 발생한 경우엔 500 에러로 처리하고 있다.
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler
	public ErrorResult exHandle(Exception e){
		return new ErrorResult("ex", "내부 서버 오류");
	}

	@GetMapping("/api2/members/{id}")
	public MemberDto getMember(@PathVariable("id") String id) {
		if (id.equals("run")) {
			throw new RuntimeException("잘못된 사용자");			//
		}
		if (id.equals("iae")) {
			throw new IllegalArgumentException("잘못된 입력 값");	// IAE가 발생함에 따라 @ExceptionHandler(IllegalArgumentException.class) 로직이 수행된다.
		}
		return new MemberDto(id, "hello " + id);
	}

	@Data
	@AllArgsConstructor
	static class MemberDto {
		private String memberId;
		private String name;
	}

}
