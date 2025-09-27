package com.shelfconnect.service;

import com.shelfconnect.dto.req.BookReq;
import com.shelfconnect.model.Book;
import com.shelfconnect.model.User;
import com.shelfconnect.util.BookReqParamParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IBookService {
    Page<Book> getAllBooks(Pageable pageable, User owner);

    Page<Book> getAllBooks(BookReqParamParser.ParamReq paramReq);
    Book getBookById(Long id);

    Book addBook(BookReq bookReq, Long ownerId);

    Book updateBook(Book book, User owner);

    boolean deleteBook(Long id, User owner);
}
