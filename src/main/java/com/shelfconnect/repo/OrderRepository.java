package com.shelfconnect.repo;

import com.shelfconnect.model.Order;
import com.shelfconnect.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    Page<Order> findAllBySeller(User seller, Pageable pageable);
}
