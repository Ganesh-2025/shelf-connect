package com.shelfconnect.dto.res;

import com.shelfconnect.dto.ItemDTO;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderRes {
    private Long id;
    private ItemDTO itemDTO;
    private boolean status;
    private BigDecimal actualAmount;
    private BigDecimal totalAmount;
}
