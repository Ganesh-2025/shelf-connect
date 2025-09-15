package com.shelfconnect.dto;

import com.shelfconnect.model.Item;
import com.shelfconnect.model.Order;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderDTO {
    private Long id;
    private ItemDTO item;
    private Order.Status status;

    public static OrderDTO from(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .status(order.getStatus())
                .item(ItemDTO.from(order.getItem()))
                .build();
    }
}
