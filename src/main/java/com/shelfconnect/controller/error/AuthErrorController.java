package com.shelfconnect.controller.error;

import com.shelfconnect.dto.api.APIResponse;
import com.shelfconnect.dto.api.Status;
import io.jsonwebtoken.JwtException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthErrorController {
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<APIResponse> handleException(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                resBuilder()
                        .message("email not found")
                        .data(Map.of("errors", Map.of("email", "email not found")))
                        .build()
        );

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<APIResponse> handleError(BadCredentialsException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                resBuilder()
                        .statusCode(HttpStatus.BAD_REQUEST)
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<APIResponse> handleError(JwtException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                resBuilder()
                        .message("Authorization token not valid. login again to continue.")
                        .build()
        );
    }

    private APIResponse.APIResponseBuilder resBuilder() {
        return APIResponse.builder()
                .status(Status.ERROR)
                .statusCode(HttpStatus.UNAUTHORIZED);
    }
}
