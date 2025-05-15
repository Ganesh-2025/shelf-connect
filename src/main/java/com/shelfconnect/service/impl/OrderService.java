package com.shelfconnect.service.impl;

import com.shelfconnect.dto.OrderDTO;
import com.shelfconnect.model.Book;
import com.shelfconnect.model.Item;
import com.shelfconnect.model.Order;
import com.shelfconnect.model.User;
import com.shelfconnect.repo.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final BookService bookService;

    public OrderService(OrderRepository orderRepository, BookService bookService) {
        this.orderRepository = orderRepository;
        this.bookService = bookService;
    }

    public Page<Order> getSellerOrders(User seller, Pageable pageable) {
        return orderRepository.findAllBySeller(seller, pageable);
    }

    @Transactional
    public Order create(User buyer, OrderDTO orderDTO) {
        Order order = Order.builder()
                .items(List.of())
                .status(Order.Status.PENDING)
                .buyer(buyer)
                .build();
        List<Item> items = orderDTO
                .getItems()
                .stream()
                .map((itemDTO) -> {

                    Book b = bookService.getBookByID(itemDTO.getBookID()).orElseThrow(() -> new RuntimeException("book not found"));
                    if (b.getOwner().getId().equals(buyer.getId()))
                        throw new RuntimeException("you cannot buy your own book");
                    if (b.getQuantity() < itemDTO.getQuantity()) throw new RuntimeException("quantity invalid");

                    Item item = Item.builder()
                            .book(b)
                            .order(order)
                            .quantity(itemDTO.getQuantity())
                            .build();

                    BigDecimal itemActualPrice = b.getActualPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    BigDecimal itemTotalPrice = b.getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    order.setActualAmount(order.getActualAmount().add(itemActualPrice));
                    order.setTotalAmount(order.getTotalAmount().add(itemTotalPrice));

                    return item;
                }).toList();
        order.setItems(items);
        return orderRepository.save(order);
    }
}
