package com.shelfconnect.controller;

import com.shelfconnect.dto.CartDTO;
import com.shelfconnect.dto.api.APIResponse;
import com.shelfconnect.dto.api.Status;
import com.shelfconnect.model.Cart;
import com.shelfconnect.security.user.UserDetails;
import com.shelfconnect.service.impl.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController("/api/cart")
@Validated
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<APIResponse> getCart(
            @AuthenticationPrincipal UserDetails userDetails
            ){
        Cart cart = cartService.getCart(userDetails.getUser());
        return ResponseEntity.ok(
                APIResponse
                        .builder()
                        .status(Status.SUCCESS)
                        .statusCode(HttpStatus.OK)
                        .message("cart updated")
                        .data(Map.of("cart",CartDTO.from(cart)))
                        .build()
        );
    }

    public ResponseEntity<APIResponse> updateCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @NotNull @Valid CartDTO cartDTO
    ){
        Cart cart = cartService.updateCart(userDetails.getUser(),cartDTO);
        return ResponseEntity.ok(
                APIResponse
                        .builder()
                        .status(Status.SUCCESS)
                        .statusCode(HttpStatus.OK)
                        .message("cart updated")
                        .data(Map.of("cart",CartDTO.from(cart)))
                        .build()
        );
    }

}
