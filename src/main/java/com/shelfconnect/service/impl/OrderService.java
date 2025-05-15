package com.shelfconnect.service.impl;

import com.shelfconnect.model.Book;
import com.shelfconnect.model.Order;
import com.shelfconnect.model.User;
import com.shelfconnect.repo.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final BookService bookService;

    public OrderService(OrderRepository orderRepository, BookService bookService) {
        this.orderRepository = orderRepository;
        this.bookService = bookService;
    }

    public Page<Order> getSellerOrders(User seller, Pageable pageable){
        return orderRepository.findAllBySeller(seller,pageable);
    }
    public void create(User buyer, List<Long> bookIDs){
        List<Book> book = bookIDs.stream()
                .map((id)->{
                    Book b = bookService.getBookByID(id).orElseThrow(()->new RuntimeException("book not found"));
                    if(b.getOwner().getId().equals(buyer.getId())) throw new RuntimeException("you cannot buy your own book");
                    return b;
                }).toList();
        Order order = Order.builder()
                .actualAmount()
                .build();
    }
}
