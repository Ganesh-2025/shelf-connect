package com.shelfconnect.dto.res;

import com.shelfconnect.model.Book;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class AllBooksRes {
    private List<Book> books;
    private long totalBooks;
    private int totalPages;
    private int pageSize;
    public static AllBooksRes from(Page<Book> books){
        return AllBooksRes.builder()
                .books(books.getContent())
                .totalBooks(books.getTotalElements())
                .pageSize(books.getSize())
                .totalPages(books.getTotalPages())
                .build();

    }
}
