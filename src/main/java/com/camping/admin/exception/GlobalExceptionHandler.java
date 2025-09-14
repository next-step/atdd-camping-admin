package com.camping.admin.exception;

import com.camping.admin.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 예외들 - 비즈니스 로직 에러 (500 Internal Server Error)
    @ExceptionHandler({
            ProductNotRentalException.class,
            InsufficientStockException.class,
            RentalAlreadyReturnedException.class,
            ProductNotFoundException.class,  // 비즈니스 로직에서 상품을 찾을 수 없는 경우
            ReservationNotFoundException.class,  // 비즈니스 로직에서 예약을 찾을 수 없는 경우
            RentalNotFoundException.class  // 비즈니스 로직에서 대여를 찾을 수 없는 경우
    })
    public ResponseEntity<ApiResponse<Void>> handleBusinessLogicException(RuntimeException ex) {
        ApiResponse<Void> response = ApiResponse.error("BUSINESS_LOGIC_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 유효성 검증 예외들 - 클라이언트 요청 오류 (400 Bad Request)
    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidQuantityException(InvalidQuantityException ex) {
        ApiResponse<Void> response = ApiResponse.error("INVALID_QUANTITY", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 기본 예외들 - 호환성을 위해 Map 기반 응답 유지 (향후 마이그레이션 예정)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> errorResponse = Map.of(
                "error", "Bad Request",
                "message", ex.getMessage(),
                "status", HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(IllegalStateException ex) {
        Map<String, Object> errorResponse = Map.of(
                "error", "Bad Request",
                "message", ex.getMessage(),
                "status", HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> errorResponse = Map.of(
                "error", "Internal Server Error",
                "message", ex.getMessage(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> errorResponse = Map.of(
                "error", "Internal Server Error",
                "message", "An unexpected error occurred",
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}