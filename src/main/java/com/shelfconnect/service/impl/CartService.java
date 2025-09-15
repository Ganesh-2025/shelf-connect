package com.shelfconnect.service.impl;

import com.shelfconnect.Exception.APIException;
import com.shelfconnect.dto.CartDTO;
import com.shelfconnect.dto.ItemDTO;
import com.shelfconnect.model.Book;
import com.shelfconnect.model.Cart;
import com.shelfconnect.model.Item;
import com.shelfconnect.model.User;
import com.shelfconnect.repo.BookRepository;
import com.shelfconnect.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CartService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CartService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public Cart updateCart(User buyer, CartDTO cartDTO) {
        Cart cart = buyer.getCart();
        List<Item> newItems = new ArrayList<>();
        Map<Long, ItemDTO> updatedItems = cartDTO
                .getCartItems().stream()
                .map(itemDTO -> {
                    if (itemDTO.getId() == null) {
                        Book book = bookRepository.findById(itemDTO.getBookID()).orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, "Book not found with id : " + itemDTO.getBookID()));
                        newItems.add(
                                Item.builder()
                                        .book(book)
                                        .quantity(itemDTO.getQuantity())
                                        .cart(cart)
                                        .build()
                        );
                        return null;
                    }
                    return itemDTO;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(ItemDTO::getId, itemDTO -> itemDTO));

        Map<Long,Item> itemsToRemove = cart.getCartItems()
                .stream()
                .map(item -> {
                    if(updatedItems.containsKey(item.getId())){
                        ItemDTO itemDTO = updatedItems.get(item.getId());
                        item.setQuantity(itemDTO.getQuantity());
                        return null;
                    }else return item;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Item::getId,item -> item));
        for(Item item : itemsToRemove.values()){
            cart.getCartItems().remove(item);
        }
        buyer =  userRepository.save(buyer);
        return buyer.getCart();
    }

    public Cart getCart(User user) {
        return user.getCart();
    }
}
