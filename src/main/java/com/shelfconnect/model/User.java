package com.shelfconnect.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_user_phone_no_email", columnNames = {"phone_no", "email"})

})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private String password;

    @Column(unique = true)
    private String phone_no;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    @Builder.Default
    private boolean isActive = true;
    @Builder.Default
    private boolean isVerified = false;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "avatar")
    private Image avatar;

    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(
            mappedBy = "owner",
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    private List<Book> books;

    public enum Role {USER, ADMIN}

}
