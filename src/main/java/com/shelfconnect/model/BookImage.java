package com.shelfconnect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BookImage {
    @Id
    @Column(name = "image_id")
    private Long imageId;
    @OneToOne(orphanRemoval = true,cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "image_id")
    private Image image;
    @ManyToOne()
    @JoinColumn(name = "book_id")
    private Book book;
    private boolean isThumbnail;
}
