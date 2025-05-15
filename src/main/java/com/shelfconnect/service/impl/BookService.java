package com.shelfconnect.service.impl;

import com.shelfconnect.dto.req.BookReq;
import com.shelfconnect.model.*;
import com.shelfconnect.repo.BookRepository;
import com.shelfconnect.repo.CategoryRepository;
import com.shelfconnect.repo.UserRepository;
import com.shelfconnect.service.IBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService implements IBookService {
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository, UserRepository userRepository, UserService userService, CategoryRepository categoryRepository, ImageService imageService) {
        this.bookRepository = bookRepository;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
        this.imageService = imageService;
    }

    @Override
    public List<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).toList();
    }

    @Override
    public List<Book> getAllBooks(
            Pageable pageable,
            User owner
    ) {
        return bookRepository.findAllByOwner(owner, pageable);
    }

    @Override
    public Page<Book> getAllBooks(
            Pageable pageable,
            Example<Book> bookExample
    ) {
        return bookRepository.findAll(bookExample, pageable);
//        return bookRepository.findAll(pageable).toList();
    }


    @Override
    public Book getBookById(Long id) {
        return bookRepository.getReferenceById(id);
    }


    @Override
    public Book updateBook(Book book, User owner) {
        bookRepository.findByIdAndOwnerId(book.getId(), owner.getId())
                .orElseThrow();
        return bookRepository.save(book);
    }

    @Override
    public boolean deleteBook(Long id, User owner) {
        bookRepository.findByIdAndOwnerId(id, owner.getId())
                .orElseThrow();
        bookRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional()
    public Book addBook(BookReq book, Long ownerId) {
        User owner = userService.getUserById(ownerId).orElseThrow();
//        SELECT ADDRESS FROM OWNER ADDRESSES OR ELSE THROW EXCEPTION
        Address address = owner.getAddresses()
                .stream()
                .filter(add -> add.getId().equals(book.getAddressId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Address not Found"));

//        FIND CATEGORY BY ID OR ELSE THROW EXCEPTION
        Category category = categoryRepository
                .findById(book.getCategoryId())
                .orElseThrow(() -> new RuntimeException("category not found"));

        List<BookImage> images = book.getImageIDs().stream()
                .map(id -> BookImage.builder()
                        .image(imageService.findById(id).orElse(null))
                        .build()
                )
                .collect(Collectors.toList());

        if (!images.isEmpty())
            images.get(0).setThumbnail(true);

//        CREATE BOOK ENTITY AND SAVE IN DATABASE
        Book newBook = Book.builder()
                .condition(book.getCondition())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .isbn(book.getIsbn())
                .author(book.getAuthor())
                .publication(book.getPublication())
                .quantity(book.getQuantity())
                .actualPrice(book.getActualPrice())
                .sellingPrice(book.getSellingPrice())
                .description(book.getDescription())
                .categories(List.of(category))
                .images(images)
                .address(address)
                .owner(owner)
                .build();

        return bookRepository.save(newBook);
    }
@Transactional()
    public Book update(BookReq book, Long ownerID) {

        User owner = userService
                .getUserById(ownerID)
                .orElseThrow(() -> new RuntimeException("owner not found"));

        Book bookToUpdate = owner.getBooks().stream()
                .filter(b -> b.getId().equals(book.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("book not found"));

        bookToUpdate.setIsbn(book.getIsbn());
        bookToUpdate.setTitle(book.getTitle());
        bookToUpdate.setAuthor(book.getAuthor());
        bookToUpdate.setPublication(book.getPublication());
        bookToUpdate.setActualPrice(book.getActualPrice());
        bookToUpdate.setSellingPrice(book.getSellingPrice());
        bookToUpdate.setDescription(book.getDescription());
        bookToUpdate.setQuantity(book.getQuantity());
        bookToUpdate.setCondition(book.getCondition());

        Category category = categoryRepository.getReferenceById(book.getCategoryId());
        bookToUpdate.setCategories(List.of(category));

        List<BookImage> images = bookToUpdate.getImages();

        List<BookImage> newImages = book.getImageIDs()
                .stream()
                .filter(id ->
                        images
                                .stream()
                                .noneMatch(bookImage -> bookImage.getImage().getId().equals(id))
                )
                .map(newId -> BookImage
                        .builder()
                        .image(imageService.findById(newId).orElse(null))
                        .build()
                )
                .filter(bookImage -> bookImage.getImage() != null)
                .toList();

        List<Image> imagesToDestroy = images
                .stream()
                .filter(image -> book.getImageIDs()
                        .stream()
                        .anyMatch(id -> image.getImageId().equals(id))
                )
                .map(bookImage -> {
                    images.remove(bookImage);
                    return bookImage.getImage();
                })
                .toList();

        List<BookImage> updatedImages = new ArrayList<>(images);
        updatedImages.addAll(newImages);

        if(!book.getImageIDs().isEmpty()){
            Long id = book.getImageIDs().get(0);
            updatedImages.stream()
                    .peek(bookImage -> bookImage.setThumbnail(false))
                    .filter(bookImage -> bookImage.getImageId().equals(id))
                    .findFirst()
                    .ifPresent(bookImage -> bookImage.setThumbnail(true));
        }

        bookToUpdate.setImages(updatedImages);

        Book updatedBook = bookRepository.save(bookToUpdate);
        imagesToDestroy.forEach(image -> {
            try {
                imageService.delete(image.getId());
            } catch (IOException e) {
                throw new RuntimeException("unable to destroy image");
            }
        });
        return updatedBook;
    }
    @Transactional
    public void delete(Long id,User owner){
        Book book = owner.getBooks().stream()
                    .filter(b->b.getId().equals(id))
                    .findFirst()
                    .orElseThrow(()->new RuntimeException("book not found"));
        book.getImages()
                .forEach(bookImage -> {
                    try {
                        imageService.delete(bookImage.getImageId());
                    } catch (IOException e) {
                        throw new RuntimeException("unable to delete image");
                    }
                });
        bookRepository.delete(book);
    }

    public Optional<Book> getBookByID(Long id){
        return bookRepository.findById(id);
    }
}
