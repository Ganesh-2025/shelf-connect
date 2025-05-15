package com.shelfconnect.dto;

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
}
