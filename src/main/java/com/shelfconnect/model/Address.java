package com.shelfconnect.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.geo.Point;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50,nullable = false)
    private String area;
    @Column(length = 15,nullable = false)
    private String city;
    @Column(length = 15,nullable = false)
    private String state;
    @Column(length = 15,nullable = false)
    private String country;
    @Column(length = 6, columnDefinition = "char(6)",nullable = false)
    private String pincode;
    @Column(columnDefinition = "POINT SRID 4326", nullable = false)
    private Point location;
    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
}
