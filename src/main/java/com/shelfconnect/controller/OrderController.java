package com.shelfconnect.controller;

import com.shelfconnect.dto.OrderDTO;
import com.shelfconnect.dto.api.APIResponse;
import com.shelfconnect.dto.api.Status;
import com.shelfconnect.model.Order;
import com.shelfconnect.security.user.UserDetails;
import com.shelfconnect.service.impl.OrderService;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.server.PathParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place-order")
    @ResponseBody
    public ResponseEntity<APIResponse> placeOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OrderDTO orderDTO
    ) {
        Order order = orderService.placeOrder(userDetails.getUser(), orderDTO);
        return ResponseEntity.ok(
                APIResponse.builder()
                        .statusCode(HttpStatus.OK)
                        .status(Status.SUCCESS)
                        .message("order created")
                        .data(OrderDTO.from(order))
                        .build()
        );
    }

    @DeleteMapping("/update-order/{id}")
    @ResponseBody
    public ResponseEntity<APIResponse> updateOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathParam("id") @NotNull Long orderID,
            @NotNull @RequestBody(required = true) Order.Status status
    ) {
        Order order = orderService.updateOrder(userDetails.getUser(), orderID, status);
        return ResponseEntity.ok(
                APIResponse.builder()
                        .statusCode(HttpStatus.OK)
                        .status(Status.SUCCESS)
                        .message("order updated")
                        .data(Map.of("order",OrderDTO.from(order)))
                        .build()
        );
    }
    @GetMapping("/my-orders")
    @ResponseBody
    public ResponseEntity<APIResponse> myOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable
    ) {
        Page<OrderDTO> orders = orderService.getMyOrders(userDetails.getUser(), pageable);
        return ResponseEntity.ok(
                APIResponse
                        .builder()
                        .statusCode(HttpStatus.OK)
                        .status(Status.SUCCESS)
                        .message("my orders")
                        .data(Map.of("orders",orders))
                        .build()
        );

    }



    @GetMapping("/my-selling-orders")
    @ResponseBody
    public ResponseEntity<APIResponse> mySellingOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable
    ) {
        Page<OrderDTO> orders = orderService.getMySellingOrders(userDetails.getUser(), pageable);
        return ResponseEntity.ok(
                APIResponse
                        .builder()
                        .statusCode(HttpStatus.OK)
                        .status(Status.SUCCESS)
                        .message("my selling orders")
                        .data(Map.of("orders",orders))
                        .build()
        );
    }

}
