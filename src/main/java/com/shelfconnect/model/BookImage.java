package com.shelfconnect.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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
    @ToString.Exclude
    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "book_id")
    private Book book;
    private boolean isThumbnail;
}
