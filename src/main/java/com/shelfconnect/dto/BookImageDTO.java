package com.shelfconnect.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class BookImageDTO {
    private MultipartFile imageFile;
    private boolean isTumbnail;
}
