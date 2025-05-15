package com.shelfconnect.controller.error;

import com.shelfconnect.Exception.APIException;
import com.shelfconnect.Exception.AppException;
import com.shelfconnect.dto.api.APIResponse;
import com.shelfconnect.dto.api.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppErrorController {
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> handleError(APIException ex) {
        ex.printStackTrace();
        System.out.println(ex.getMessage());

        return ResponseEntity.status(ex.getStatusCode()).body(
                resBuilder()
                        .statusCode(ex.getStatusCode())
                        .message(ex.getMessage())
                        .data(ex.getData())
                        .build()
        );
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<APIResponse> handleError(AppException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                resBuilder()
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse> handleError(Exception ex) {

        ex.printStackTrace();
        System.out.println(ex.getClass().getName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                resBuilder()
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                        .message("Unknown Error Occurred")
                        .build()
        );
    }

    private APIResponse.APIResponseBuilder resBuilder() {
        return APIResponse.builder()
                .status(Status.ERROR);
    }
}
