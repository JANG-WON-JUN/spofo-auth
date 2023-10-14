package net.spofo.auth.exception.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorResult {

    private final String errorCode;
    private final String errorMessage;

    @Builder
    private ErrorResult(HttpStatus errorCode, String errorMessage) {
        this.errorCode = String.valueOf(errorCode.value());
        this.errorMessage = errorMessage;
    }

}