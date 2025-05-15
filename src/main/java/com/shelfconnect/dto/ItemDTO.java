package com.shelfconnect.dto;

import com.shelfconnect.model.Item;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDTO {
    private Long id;
    @NotNull
    private Long bookID;
    @Positive
    @NotNull
    private int quantity;
    public static ItemDTO from(Item item){
        return ItemDTO.builder()
                .id(item.getId())
                .bookID(item.getBook().getId())
                .quantity(item.getQuantity())
                .build();
    }
}
