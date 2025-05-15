package com.shelfconnect.controller.error;

import com.shelfconnect.dto.api.APIResponse;
import com.shelfconnect.dto.api.Status;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DBErrorController {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<APIResponse> handleError(DataIntegrityViolationException ex) {
        ex.printStackTrace();
        Map<Object, Object> data = null;
        String message = ex.getMostSpecificCause().getMessage();
        if (message.contains("uk_user_email")) {
            data = new HashMap<>();
            data.put("email", "email should be unique");
        } else if (message.contains("uk_user_phone_no_email")) {
            data = new HashMap<>();
            data.put("phone_no", "phone number should be unique");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                resBuilder()
                        .statusCode(HttpStatus.BAD_REQUEST)
                        .message("error occurred")
                        .data(data)
                        .build()
        );
    }

    private APIResponse.APIResponseBuilder resBuilder() {
        return APIResponse.builder()
                .status(Status.ERROR);
    }
}
