package com.shelfconnect.dto;

import com.shelfconnect.model.Order;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderDTO {
    private List<ItemDTO> items;
    private BigDecimal actualAmount;
    private BigDecimal totalAmount;
    private Order.Status status;

    public static OrderDTO from(Order order) {
        return OrderDTO.builder()
                .actualAmount(order.getActualAmount())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .items(
                        order
                                .getItems()
                                .stream()
                                .map(ItemDTO::from)
                                .toList()
                )
                .build();
    }
}
