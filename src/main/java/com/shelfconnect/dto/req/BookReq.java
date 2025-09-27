package com.shelfconnect.dto.req;

import com.shelfconnect.constant.AppConstants;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Data
public class BookReq {
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
    @Size(min = 1,message = "address required")
    private List<@NotNull Long> addressIDs;
    @NotNull
    @Size(min = 1,message = "book image required")
    private List<@NotNull  Long> imageIDs;

    @AssertTrue(message = "invalid condition ")
    public boolean isConditionValid() {
        return condition != null && Arrays.stream(AppConstants.bookConditions).anyMatch(condition -> condition.equals(this.condition));
    }

}
