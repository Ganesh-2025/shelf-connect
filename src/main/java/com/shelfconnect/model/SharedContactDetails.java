package com.shelfconnect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedContactDetails {
    @Id
    private Long ID;
    @ManyToOne()
    @JoinColumn(nullable = false)
    private User from;
    @ManyToOne()
    @JoinColumn(nullable = false)
    private User to;
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "TEXT")
    private String details;

    public  enum Status{ACCEPTED,DENIED,PENDING};
}
