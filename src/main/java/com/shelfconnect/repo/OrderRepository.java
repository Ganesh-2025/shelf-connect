package com.shelfconnect.repo;

import com.shelfconnect.dto.OrderDTO;
import com.shelfconnect.model.Order;
import com.shelfconnect.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {
    Optional<Order> findOrderByIdAndBuyer(Long id, User buyer);

   Optional<Order> findOrderByIdAndSeller(Long id, User seller);

    Page<OrderDTO> findAllBySeller(User seller, Pageable pageable);

    Page<OrderDTO> findAllByBuyer(User buyer, Pageable pageable);
}
