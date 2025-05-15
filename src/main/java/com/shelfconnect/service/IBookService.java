package com.shelfconnect.service;

import com.shelfconnect.dto.req.BookReq;
import com.shelfconnect.model.Book;
import com.shelfconnect.model.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IBookService {
    List<Book> getAllBooks(Pageable pageable, User owner);

    Page<Book> getAllBooks(
            Pageable pageable,
            Example<Book> bookExample
    );

    List<Book> getAllBooks(
            Pageable pageable
    );

    Book getBookById(Long id);

    Book addBook(BookReq bookReq, Long ownerId);

    Book updateBook(Book book, User owner);

    boolean deleteBook(Long id, User owner);
}
