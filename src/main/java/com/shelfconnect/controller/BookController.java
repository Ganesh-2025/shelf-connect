package com.shelfconnect.controller;

import com.shelfconnect.dto.api.APIResponse;
import com.shelfconnect.dto.api.Status;
import com.shelfconnect.dto.req.BookReq;
import com.shelfconnect.dto.res.SingleBookRes;
import com.shelfconnect.model.Book;
import com.shelfconnect.model.Category;
import com.shelfconnect.repo.CategoryRepository;
import com.shelfconnect.security.user.UserDetails;
import com.shelfconnect.service.impl.BookService;
import com.shelfconnect.service.impl.ImageService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/book")
@Validated
public class BookController {
    private final BookService bookService;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;

    public BookController(BookService bookService, CategoryRepository categoryRepository, ImageService imageService) {
        this.bookService = bookService;
        this.categoryRepository = categoryRepository;
        this.imageService = imageService;
    }

    public List<?> getAllBooks() {
        return null;
    }
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<APIResponse> bookById(
            @PathVariable Long id
    ){
        Book book = bookService.getBookByID(id)
                .orElseThrow(()->new RuntimeException("book not found"));
        return ResponseEntity.ok(
                APIResponse.builder()
                        .statusCode(HttpStatus.OK)
                        .status(Status.SUCCESS)
                        .message("book found")
                        .data(Map.of("book",book))
                        .build()
        );
    }

    @PostMapping("/add-book")
    @ResponseBody
    public ResponseEntity<APIResponse> addBook(
            @AuthenticationPrincipal UserDetails authUser,
            @Valid @RequestBody BookReq book
    ) {
        Book newBook = bookService.addBook(book, authUser.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                APIResponse.builder()
                        .statusCode(HttpStatus.CREATED)
                        .status(Status.SUCCESS)
                        .message("book added")
                        .data(SingleBookRes.from(newBook))
                        .build()
        );
    }

    @PutMapping("/update-book")
    @ResponseBody
    public ResponseEntity<APIResponse> updateBook(
            @AuthenticationPrincipal UserDetails authUser,
            @Valid @RequestBody BookReq book
    ){
        Book updatedBook = bookService.update(book,authUser.getUser().getId());
        return ResponseEntity.ok(
                APIResponse.builder()
                        .statusCode(HttpStatus.OK)
                        .status(Status.SUCCESS)
                        .message("book updated successfully")
                        .data(Map.of("book",updatedBook))
                        .build()
        );
    }

    @DeleteMapping("/delete-book/{id}")
    @ResponseBody
    public ResponseEntity<APIResponse> deleteBook(
            @AuthenticationPrincipal UserDetails authUser,
            @PathVariable("id") Long book_id
    ){
        bookService.delete(book_id,authUser.getUser());
        return ResponseEntity.ok(
                APIResponse.builder()
                        .status(Status.SUCCESS)
                        .statusCode(HttpStatus.OK)
                        .message("book deleted successfully")
                        .build()
        );
    }

    @GetMapping("/book-categories")
    @ResponseBody
    public ResponseEntity<APIResponse> getCategories(
            @Nullable @RequestParam("type") String type
    ) {
        List<Category> categories;
        if (type != null)
            categories = categoryRepository.findByType(type);
        else
            categories = categoryRepository.findAll();
        return ResponseEntity.ok(
                APIResponse.builder()
                        .status(Status.SUCCESS)
                        .statusCode(HttpStatus.OK)
                        .data(categories)
                        .build()
        );
    }


}
