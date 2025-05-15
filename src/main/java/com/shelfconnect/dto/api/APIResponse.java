package com.shelfconnect.dto.api;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
public class APIResponse {
    private HttpStatus statusCode;
    private Status status;
    private String message;
    private Object data;
    @Builder.Default
    private String timestamp = getFormattedTimestamp(new Date());

    private static String getFormattedTimestamp(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        return sdf.format(date);
    }
}
