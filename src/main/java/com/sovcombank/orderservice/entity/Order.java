package com.sovcombank.orderservice.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String ownerId;
    private String title;
    private String body;
    @ElementCollection
    Set<String> hrbps = new HashSet<>();
    @Enumerated(EnumType.STRING)
    private Status status;
    private Instant createdAt;
    private Instant closedAt;
}
