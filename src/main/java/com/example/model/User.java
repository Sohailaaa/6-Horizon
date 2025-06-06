package com.example.model;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class User {
    private UUID id = UUID.randomUUID();
    private String name;
    private List<Order> orders=new ArrayList<>();

    public User(String name , List<Order> orders) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.orders = orders;
    }
}

