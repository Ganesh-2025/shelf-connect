package com.shelfconnect.controller;

import com.shelfconnect.dto.OrderDTO;
import com.shelfconnect.dto.api.APIResponse;
import com.shelfconnect.dto.api.Status;
import com.shelfconnect.dto.res.PageRes;
import com.shelfconnect.model.Order;
import com.shelfconnect.security.user.UserDetails;
import com.shelfconnect.service.impl.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<APIResponse> sendOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OrderDTO orderDTO
    ){
        Order order = orderService.create(userDetails.getUser(),orderDTO);
        return ResponseEntity.ok(
                APIResponse.builder()
                        .statusCode(HttpStatus.OK)
                        .status(Status.SUCCESS)
                        .message("order created")
                        .data(OrderDTO.from(order))
                        .build()
        );
    }

    @GetMapping("/seller/")
    @ResponseBody
    public ResponseEntity<APIResponse> getSellerOrders(
            @AuthenticationPrincipal UserDetails userDetails
            ){
        Page<Order> orders = orderService.getSellerOrders(userDetails.getUser(), Pageable.ofSize(10));
        return ResponseEntity.ok(
                APIResponse
                        .builder()
                        .statusCode(HttpStatus.OK)
                        .status(Status.SUCCESS)
                        .message("seller orders")
                        .data(PageRes.from(orders))
                        .build()
        );

    }

}
