package com.project.global.error;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

@Slf4j // 롬복을 이용한 로깅
@RestControllerAdvice // 모든 @RestController에서 발생하는 예외를 가로챕니다.
public class GlobalExceptionHandler {

    // 1. Bean Validation (400) 에러 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        // 여러 에러 중 첫 번째 에러 메시지만 추출하여 클라이언트에게 전달
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("Validation Error: {}", errorMessage);
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("INVALID_INPUT", errorMessage));
    }
    
    //상태코드가 있는 경우 ResponseStatusException을 활용하여 에러 처리
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e) {
        log.warn("비즈니스 예외 발생: {} - {}", e.getStatusCode(), e.getReason());
        
        return ResponseEntity
                .status(e.getStatusCode())
                .body(ErrorResponse.of(
                        e.getStatusCode().toString(), 
                        e.getReason()
                ));
    }

    // 2. 비즈니스 로직 (400) 커스텀 에러 처리 (예: IllegalArgumentException)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Business Logic Error: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("BUSINESS_ERROR", e.getMessage()));
    }

    // 3. 알 수 없는 서버 (500) 에러 처리 (최후의 보루)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception e) {
        // 1. ResponseStatusException 타입인 경우 (이미 작성하신 로직)
        if (e instanceof ResponseStatusException resEx) {
            return handleResponseStatusException(resEx);
        }

        // 2. 클래스에 @ResponseStatus 어노테이션이 붙은 경우 추출
        ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(e.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            return ResponseEntity
                    .status(responseStatus.code())
                    .body(ErrorResponse.of(responseStatus.code().toString(), e.getMessage()));
        }

        // 3. 진짜 알 수 없는 500 에러
        log.error("Unhandled Exception: ", e); 
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."));
    }
    
    // GlobalExceptionHandler.java 내부에 추가
    @ExceptionHandler(NeedRegistrationException.class)
    public ResponseEntity<ErrorResponse> handleNeedRegistrationException(NeedRegistrationException e) {
        // 1. 에러 응답 객체 생성 (기존에 만드신 ErrorResponse 형식을 따름)
        ErrorResponse errorResponse = ErrorResponse.of("NEED_REGISTRATION", e.getMessage());
        
        // 2. 404 상태 코드와 함께 반환 (프론트엔드가 catch 문으로 빠지게 함)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
}