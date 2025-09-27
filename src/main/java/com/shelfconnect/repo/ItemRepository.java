package com.shelfconnect.repo;

import com.shelfconnect.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository< Item,Long> {
    @Query("select i from Item i join Book b on(i.book.id = b.id) join User u on(b.owner.id=u.id) where u.id = :sellerID")
    List<Item> getSellerItems(@Param("sellerID") Long sellerID);

}
