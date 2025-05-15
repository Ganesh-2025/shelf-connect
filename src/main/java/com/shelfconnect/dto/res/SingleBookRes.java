package com.shelfconnect.dto.res;

import com.shelfconnect.dto.AddressDTO;
import com.shelfconnect.model.Book;
import com.shelfconnect.model.Category;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class SingleBookRes {
    private String title;
    private String isbn;
    private String description;
    private String publication;
    private String author;
    private List<Category> categories;
    private String condition;
    private BigDecimal actualPrice;
    private BigDecimal sellingPrice;
    private int quantity;
    private List<String> images;
    private AddressDTO addressDTO;

    public static SingleBookRes from(Book book) {
        SingleBookRes singleBookRes =  SingleBookRes.builder()
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .publication(book.getPublication())
                .author(book.getAuthor())
                .categories(book.getCategories())
                .condition(book.getCondition())
                .actualPrice(book.getActualPrice())
                .sellingPrice(book.getSellingPrice())
                .quantity(book.getQuantity())
                .addressDTO(AddressDTO.from(book.getAddress()))
                .build();
        if(book.getImages()!=null){
            singleBookRes.setImages(
                    book.getImages()
                            .stream()
                            .map(img -> img.getImage().getUrl())
                            .toList()
            );
        }
        return singleBookRes;
    }
}
