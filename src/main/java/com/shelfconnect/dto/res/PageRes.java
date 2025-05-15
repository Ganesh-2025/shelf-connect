package com.shelfconnect.dto.res;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class PageRes {
    private List<?> content;
    private int pageNo;
    private long totalElements;
    private int totalPages;
    private int pageSize;

    public static PageRes from(Page<?> page) {
        return builder()
                .content(page.getContent())
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }
}
