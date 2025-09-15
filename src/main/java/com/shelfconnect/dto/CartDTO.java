package com.shelfconnect.dto;

import com.shelfconnect.model.Cart;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartDTO {
    @NotNull
    private Long id;
    @NotNull
    private List<ItemDTO> cartItems;
    public static CartDTO from(Cart cart){
        return CartDTO.builder()
                .id(cart.getId())
                .cartItems(cart.getCartItems().stream().map(ItemDTO::from).toList())
                .build();
    }
}
