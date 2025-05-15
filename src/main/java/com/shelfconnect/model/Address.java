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
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
}
