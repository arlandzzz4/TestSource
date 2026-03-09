package com.project.global.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
        log.error("Internal Server Error: ", e); // 500 에러는 반드시 StackTrace를 로그로 남겨야 합니다.
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("SERVER_ERROR", "서버 내부에서 일시적인 오류가 발생했습니다."));
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