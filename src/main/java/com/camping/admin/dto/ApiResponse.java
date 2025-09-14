package com.camping.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ErrorResponse error;
    private LocalDateTime timestamp;

    private ApiResponse(boolean success, T data, ErrorResponse error) {
        this.success = success;
        this.data = data;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

    // 성공 응답 생성 메서드들
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, null);
    }

    // 실패 응답 생성 메서드들
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, ErrorResponse.of(code, message));
    }

    public static <T> ApiResponse<T> error(ErrorResponse error) {
        return new ApiResponse<>(false, null, error);
    }

    // 기존 Map 기반 응답과의 호환성을 위한 메서드
    @Deprecated
    public static <T> ApiResponse<T> fromLegacyError(String error, String message, int status) {
        String code = mapStatusToCode(status);
        return new ApiResponse<>(false, null, ErrorResponse.of(code, message));
    }

    private static String mapStatusToCode(int status) {
        return switch (status) {
            case 400 -> "BAD_REQUEST";
            case 404 -> "NOT_FOUND";
            case 500 -> "INTERNAL_SERVER_ERROR";
            default -> "UNKNOWN_ERROR";
        };
    }
}