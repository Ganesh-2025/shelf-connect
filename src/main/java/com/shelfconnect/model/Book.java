package com.shelfconnect.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 13)
    private String isbn;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 20)
    private String author;

    @Column(length = 30)
    private String publication;

    @Column(precision = 8, scale = 2, nullable = false)
    private BigDecimal actualPrice;

    @Column(precision = 8, scale = 2, nullable = false)
    private BigDecimal sellingPrice;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "int check(quantity > 0)")
    private int quantity;
    @Column(name = "`condition`", nullable = false)
    private String condition;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "book", fetch = FetchType.LAZY,orphanRemoval = true)
    private List<BookImage> images;


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "book_address",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id")
    )
    private List<Address> addresses;
    private Timestamp createdAt;
}
