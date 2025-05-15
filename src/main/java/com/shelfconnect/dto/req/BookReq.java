package com.shelfconnect.dto.req;

import com.shelfconnect.dto.BookImageDTO;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Data
public class BookReq {
    private static final String[] conditions = {"EXCELLENT", "GOOD", "AVERAGE", "BELOW AVERAGE", "POOR"};
    private Long id;
    @NotBlank
    @Size(max = 50)
    private String title;
    @NotBlank
    @Size(min = 13, max = 13)
    private String isbn;
    @NotBlank
    @Size(max = 200)
    private String description;
    @NotBlank
    private String condition;
    @Positive
    private int quantity;
    @PositiveOrZero
    private BigDecimal sellingPrice;
    @PositiveOrZero
    private BigDecimal actualPrice;
    private String author;
    private String publication;
    @NotNull
    private Long categoryId;
    @NotNull
    private Long addressId;

    private List<Long> imageIDs;

    @AssertTrue(message = "invalid condition ")
    public boolean isConditionValid() {
        return condition != null && Arrays.stream(conditions).anyMatch(condition -> condition.equals(this.condition));
    }

}
