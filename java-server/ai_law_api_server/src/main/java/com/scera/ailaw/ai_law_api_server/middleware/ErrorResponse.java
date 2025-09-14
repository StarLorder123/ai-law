package com.scera.ailaw.ai_law_api_server.middleware;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int code;
    private String message;
    private LocalDateTime timestamp;

    public static ErrorResponse of(int code, String message) {
        return new ErrorResponse(code, message, LocalDateTime.now());
    }
}

