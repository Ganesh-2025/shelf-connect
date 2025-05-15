package com.shelfconnect.dto.res;

import com.shelfconnect.dto.ItemDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ItemsRes {
    private List<ItemDTO> items;
    private long totalBooks;
    private int totalPages;
    private int pageSize;
}
