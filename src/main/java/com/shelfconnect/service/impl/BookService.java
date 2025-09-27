package com.shelfconnect.service.impl;

import com.shelfconnect.Exception.APIException;
import com.shelfconnect.dto.req.BookReq;
import com.shelfconnect.model.*;
import com.shelfconnect.repo.BookRepository;
import com.shelfconnect.repo.CategoryRepository;
import com.shelfconnect.repo.UserRepository;
import com.shelfconnect.repo.specification.BookSpecifications;
import com.shelfconnect.service.IBookService;
import com.shelfconnect.util.BookReqParamParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService implements IBookService {
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;
    private final BookRepository bookRepository;
    private final BookSpecifications specifications;

    @Autowired
    public BookService(BookRepository bookRepository, UserRepository userRepository, UserService userService, CategoryRepository categoryRepository, ImageService imageService, BookSpecifications specifications) {
        this.bookRepository = bookRepository;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
        this.imageService = imageService;
        this.specifications = specifications;
    }


    @Override
    public Page<Book> getAllBooks(
            Pageable pageable,
            User owner
    ) {
        return bookRepository.findAllByOwner(owner, pageable);
    }

    @Override
    public Page<Book> getAllBooks(
            BookReqParamParser.ParamReq paramReq
    ) {
        int maxPageSize = 50;
        int pageNumber = Math.max(paramReq.page(), 0);
        int pageSize = paramReq.size() > 0 ? Math.min(paramReq.size(), maxPageSize) : 10;
        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                paramReq.sort() != null ? paramReq.sort() : Sort.unsorted()
        );

        List<Specification<Book>> specs = new ArrayList<>();
        if (paramReq.title() != null) specs.add(specifications.title(paramReq.title()));
        if (paramReq.author() != null) specs.add(specifications.author(paramReq.author()));
        if (paramReq.isbn() != null) specs.add(specifications.isbn(paramReq.isbn()));
        if (paramReq.categories() != null) specs.add(specifications.categories(paramReq.categories()));
        if (paramReq.maxPrice() != null) specs.add(specifications.maxPrice(paramReq.maxPrice()));
        if (paramReq.condition() != null) specs.add(specifications.condition(paramReq.condition()));
        specs.add(specifications.inStock(paramReq.inStock()));
        if (paramReq.nearby() != null) specs.add(specifications.nearBy(paramReq.nearby()));
        Specification<Book> specification = specs.stream()
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse(null);

        return bookRepository.findAll(specification, pageable);
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
        List<Address> addresses = owner.getAddresses()
                .stream()
                .filter((ownerAddress) ->
                        book.getAddressIDs()
                                .stream()
                                .anyMatch(id -> ownerAddress.getId().equals(id))
                )
                .toList();
        if (addresses.size() <= 0) throw new RuntimeException("Address not found");

//        FIND CATEGORY BY ID OR ELSE THROW EXCEPTION
        Category category = categoryRepository
                .findById(book.getCategoryId())
                .orElseThrow(() -> new RuntimeException("category not found"));


//        CREATE BOOK ENTITY AND SAVE IN DATABASE
        List<BookImage> images = new ArrayList<>();

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
                .addresses(addresses)
                .owner(owner)
                .build();

        book.getImageIDs().stream()
                .map(newId -> {
                            var image = imageService.findById(newId).orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, "Image not Found"));
                            return BookImage
                                    .builder()
                                    .image(image)
                                    .book(newBook)
                                    .build();
                        }
                )
                .forEach(images::add);
        if (images.isEmpty()) throw new APIException(HttpStatus.BAD_REQUEST, "image required");
        images.get(0).setThumbnail(true);

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

        bookToUpdate.getAddresses().clear();
        Map<Long, Address> ownerAddresses = owner.getAddresses()
                .stream()
                .collect(Collectors.toMap(Address::getId, address -> address));
        book.getAddressIDs()
                .stream()
                .filter(ownerAddresses::containsKey)
                .map(ownerAddresses::get)
                .forEach(address -> bookToUpdate.getAddresses().add(address));
//        bookToUpdate.setAddresses(new ArrayList<>(updatedAddresses));
        if (bookToUpdate.getCategories().get(0).equals(book.getCategoryId())) {
            Category category = categoryRepository.findById(book.getCategoryId()).orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, "Category not Found."));
            bookToUpdate.getCategories().clear();
            bookToUpdate.getCategories().add(category);
        }
        List<BookImage> images = bookToUpdate.getImages();
        System.out.println(images);
        List<BookImage> newImages = book.getImageIDs()
                .stream()
                .filter(id ->
                        images
                                .stream()
                                .noneMatch(bookImage -> bookImage.getImageId().equals(id))
                )
                .map(newId -> {
                            var image = imageService.findById(newId).orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, "Image not Found"));
                            return BookImage
                                    .builder()
                                    .image(image)
                                    .book(bookToUpdate)
                                    .build();
                        }
                )
                .toList();

        List<BookImage> imagesToDestroy = images
                .stream()
                .filter(image -> book.getImageIDs()
                        .stream()
                        .noneMatch(id -> image.getImageId().equals(id))
                )
                .toList();
        for (BookImage bookImage : imagesToDestroy) {
            images.remove(bookImage);
        }
        images.addAll(newImages);

        Long id = book.getImageIDs().get(0);
        for (BookImage image : images) {
            image.setThumbnail(false);
            if (image.getImage().getId().equals(id))
                image.setThumbnail(true);
        }

        Book updatedBook = bookRepository.save(bookToUpdate);
        imagesToDestroy.forEach(image -> {
            try {
                imageService.delete(image.getImage().getId());
            } catch (IOException e) {
                throw new RuntimeException("unable to destroy image");
            }
        });
        return updatedBook;
    }

    @Transactional
    public void delete(Long id, User owner) {
        Book book = owner.getBooks().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("book not found"));
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

    public Optional<Book> getBookByID(Long id) {
        return bookRepository.findById(id);
    }
}
