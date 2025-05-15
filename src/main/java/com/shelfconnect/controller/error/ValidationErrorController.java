package com.shelfconnect.controller.error;

import com.shelfconnect.dto.api.APIResponse;
import com.shelfconnect.dto.api.Status;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationErrorController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse> handleError(MethodArgumentNotValidException ex) {
        ex.printStackTrace();
        Map<Object, Object> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(FieldError::getField, f -> f.getDefaultMessage() == null ? "" : f.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                resBuilder()
                        .statusCode(HttpStatus.BAD_REQUEST)
                        .message("Validation Error")
                        .data(Map.of("errors", errors))
                        .build()
        );
    }


    private APIResponse.APIResponseBuilder resBuilder() {
        return APIResponse.builder()
                .status(Status.ERROR);
    }
}
