package com.shelfconnect.service.impl;

import com.shelfconnect.Exception.APIException;
import com.shelfconnect.dto.ItemDTO;
import com.shelfconnect.dto.OrderDTO;
import com.shelfconnect.model.Book;
import com.shelfconnect.model.Item;
import com.shelfconnect.model.Order;
import com.shelfconnect.model.User;
import com.shelfconnect.repo.ItemRepository;
import com.shelfconnect.repo.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final BookService bookService;
    private final ItemRepository itemRepository;

    public OrderService(OrderRepository orderRepository, BookService bookService, ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.bookService = bookService;
        this.itemRepository = itemRepository;
    }

    public Page<OrderDTO> getMySellingOrders(User seller, Pageable pageable) {
        return orderRepository.findAllBySeller(seller, pageable);
    }

    public Page<OrderDTO> getMyOrders(User buyer,Pageable pageable){
        return orderRepository.findAllByBuyer(buyer,pageable);
    }
    @Transactional
    public Order placeOrder(User buyer, OrderDTO orderDTO) {
        Order order = Order.builder()
                .status(Order.Status.PENDING)
                .buyer(buyer)
                .build();

        ItemDTO itemDTO = orderDTO.getItem();
        Book b = bookService.getBookByID(itemDTO.getBookID()).orElseThrow(() -> new RuntimeException("book not found"));
        if (b.getOwner().getId().equals(buyer.getId()))
            throw new RuntimeException("you cannot buy your own book");
        if (itemDTO.getQuantity()<=0 || itemDTO.getQuantity() > b.getQuantity() ) throw new RuntimeException("quantity invalid");

        Item item = Item.builder()
                .book(b)
                .order(order)
                .quantity(itemDTO.getQuantity())
                .build();

        order.setItem(item);
        order.setSeller(b.getOwner());

        return orderRepository.save(order);
    }


    public Order updateOrder(User user, Long orderID, Order.Status status){
        Order order = null;
        if(status.equals(Order.Status.DECLINED)|| status.equals(Order.Status.ACCEPTED)){
            User seller = user;
            order = orderRepository.findOrderByIdAndSeller(orderID,seller).orElseThrow(()->new APIException(HttpStatus.BAD_REQUEST,"order not found"));
            if(order.getStatus().equals(Order.Status.CANCELED)) throw new APIException(HttpStatus.BAD_REQUEST,"Order already cancelled by buyer");
            order.setStatus(status);
        }else{
            User buyer = user;
            order = orderRepository.findOrderByIdAndBuyer(orderID,buyer).orElseThrow(()->new APIException(HttpStatus.BAD_REQUEST,"order not found"));
            if(order.getStatus().equals(Order.Status.DECLINED)) throw new APIException(HttpStatus.BAD_REQUEST,"Order already declined by seller");
            order.setStatus(status);
        }
        return orderRepository.save(order);
    }
}
