package com.shelfconnect.Exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class APIException extends RuntimeException {
    private HttpStatus statusCode;
    private Object data;

    public APIException(String message, HttpStatus statusCode, Object data) {
        super(message);
        this.statusCode = statusCode;
        this.data = data;
    }

    public APIException(HttpStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public APIException() {
        super("Something went wrong");
        statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public static APIException forInternalServerError(String message) {
        return new APIException(message, HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    public static APIException forInternalServerError(String message, Object data) {
        return new APIException(message, HttpStatus.INTERNAL_SERVER_ERROR, data);
    }
}
